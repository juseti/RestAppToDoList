package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.todo.CollaboratorResponse;
import com.softserve.itacademy.dto.todo.ToDoRequest;
import com.softserve.itacademy.dto.todo.ToDoResponse;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{user_id}/todos")
public class ToDoController {

    private final ToDoService todoService;
    private final UserService userService;

    @Autowired
    public ToDoController(ToDoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }



    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #userId")
    @GetMapping
    public ResponseEntity<List<ToDoResponse>> readAll(@PathVariable("user_id") long userId) {
        return new ResponseEntity<>(
                todoService.getByUserId(userId)
                        .stream()
                        .map(ToDoResponse::getToDoResponse)
                        .collect(Collectors.toList())
                , HttpStatus.OK
        );
    }


    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #userId")
    @GetMapping("/{id}")
    public ResponseEntity<ToDoResponse> read(@PathVariable("user_id") long userId, @PathVariable("id") long id) {
        return new ResponseEntity<>(ToDoResponse.getToDoResponse(todoService.readById(id)), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #userId")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ToDoResponse> create(@PathVariable("user_id") long userId,
                                               @RequestBody ToDoRequest toDoRequest) {
        if (userService.readById(userId).getMyTodos().stream().anyMatch(t->t.getTitle().equals(toDoRequest.getTitle()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ToDo '" + toDoRequest.getTitle() + "' already exists");
        }

        ToDo toDo = new ToDo();
        toDo.setTitle(toDoRequest.getTitle());
        toDo.setOwner(userService.readById(userId));
        toDo.setCreatedAt(LocalDateTime.now());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(todoService.create(toDo).getId())
                .toUri();
        return ResponseEntity.created(location).body(ToDoResponse.getToDoResponse(toDo));
    }



    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #userId")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("user_id") long userId, @PathVariable("id") long id,
                                    @RequestBody ToDoRequest toDoRequest) {
        ToDo toDo = todoService.readById(id);
        toDo.setTitle(toDoRequest.getTitle());
        todoService.update(toDo);
        return new ResponseEntity<>("ToDo updated successfully", HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #userId")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("user_id") long userId, @PathVariable("id") long id) {
        todoService.delete(id);
        return new ResponseEntity<>("ToDo deleted successfully", HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and @toDoController.UpdateToDo(#id)")
    @GetMapping("/{id}/collaborators")
    public ResponseEntity<CollaboratorResponse> addCollaborator(@PathVariable("user_id") long userId, @PathVariable("id") long id) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();

        if (collaborators.contains(userService.readById(userId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Collaborator with id '" + userId + "'  already exists");
        }

        collaborators.add(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        return new ResponseEntity<>(CollaboratorResponse.getCollaboratorResponse(userId), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') and @toDoController.UpdateToDo(#id)")
    @GetMapping("/{id}/remove")
    public ResponseEntity<?> removeCollaborator(@PathVariable("user_id") long userId, @PathVariable("id") long id) {
        ToDo todo = todoService.readById(id);
        List<User> collaborators = todo.getCollaborators();
        collaborators.remove(userService.readById(userId));
        todo.setCollaborators(collaborators);
        todoService.update(todo);
        return new ResponseEntity<>("Collaborator removed successfully", HttpStatus.OK);
    }


    public boolean ReadToDo(Long Id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userPrincipal = (UserDetails)authentication.getPrincipal();
        ToDo todo = todoService.readById(Id);
        List <User> users = todo.getCollaborators();
        if(todo.getOwner().getEmail().equals(userPrincipal.getUsername())) {
            return true;
        } else
            for (User user: users) {
                if(user.getEmail().equals(userPrincipal.getUsername())) {
                    return true;
                }
            }
        return false;
    }

    public boolean UpdateToDo(Long Id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userPrincipal = (User)authentication.getPrincipal();
        ToDo todo = todoService.readById(Id);
        return Objects.equals(todo.getOwner().getId(), userPrincipal.getId());
    }
}
