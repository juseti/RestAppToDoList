package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.task.TaskRequest;
import com.softserve.itacademy.dto.task.TaskResponse;
import com.softserve.itacademy.model.*;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.ToDoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{user_id}/todos/{todo_id}/tasks")
public class TaskController {
    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService todoService, StateService stateService) {
        this.taskService = taskService;
        this.todoService = todoService;
        this.stateService = stateService;
    }


    @PreAuthorize("hasAuthority('ADMIN') or @toDoController.ReadToDo(#todoId)")
    @GetMapping
    public ResponseEntity<List<TaskResponse>> readAll(@PathVariable("user_id") long userId,
                                                      @PathVariable("todo_id") long todoId) {
        isOwnerOfToDo(userId, todoId);
        return new ResponseEntity<>(
                taskService.getByTodoId(todoId)
                        .stream()
                        .map(TaskResponse::getTaskResponse)
                        .collect(Collectors.toList())
                , HttpStatus.OK
        );
    }


    @PreAuthorize("hasAuthority('ADMIN') or @toDoController.ReadToDo(#todoId)")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> read(@PathVariable("user_id") long userId,
                                             @PathVariable("todo_id") long todoId,
                                             @PathVariable("id") long id) {
        isOwnerOfToDo(userId, todoId);
        return new ResponseEntity<>(TaskResponse.getTaskResponse(taskService.readById(id)), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or @toDoController.ReadToDo(#todoId)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TaskResponse> create(@PathVariable("user_id") long userId,
                                               @PathVariable("todo_id") long todoId,
                                               @RequestBody TaskRequest taskRequest) {
        isOwnerOfToDo(userId, todoId);
        isExists(todoId, taskRequest);
        Task task = new Task();
        task.setName(taskRequest.getName());
        task.setState(stateService.getByName("New"));
        task.setPriority(Priority.valueOf(taskRequest.getPriority()));
        task.setTodo(todoService.readById(todoId));

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(taskService.create(task).getId())
                .toUri();
        return ResponseEntity.created(location).body(TaskResponse.getTaskResponse(task));
    }


    @PreAuthorize("hasAuthority('ADMIN') or @toDoController.ReadToDo(#todoId)")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("user_id") long userId,
                                    @PathVariable("todo_id") long todoId,
                                    @PathVariable("id") long id,
                                    @RequestBody TaskRequest taskRequest
    ) {
        isOwnerOfToDo(userId, todoId);
        Task task = taskService.readById(id);
        task.setName(taskRequest.getName());
        task.setPriority(Priority.valueOf(taskRequest.getPriority()));
        task.setState(stateService.getByName(taskRequest.getState()));
        taskService.update(task);
        return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or @toDoController.ReadToDo(#todoId)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("user_id") long userId,
                                    @PathVariable("todo_id") long todoId,
                                    @PathVariable("id") long id) {
        isOwnerOfToDo(userId, todoId);
        taskService.delete(id);
        return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
    }


    public void isOwnerOfToDo(long userId, long todoId) throws EntityNotFoundException {
        if (todoService.readById(todoId).getOwner().getId() != userId) {
            throw new EntityNotFoundException("User id=" + userId +" don't have todo id=" + todoId);
        }
    }


    public void isExists(long todoId, TaskRequest taskRequest) throws EntityNotFoundException {
        if (taskService.getByTodoId(todoId).stream().anyMatch(t->t.getName().equals(taskRequest.getName()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Task '" + taskRequest.getName() + "' already exists");
        }
    }
}
