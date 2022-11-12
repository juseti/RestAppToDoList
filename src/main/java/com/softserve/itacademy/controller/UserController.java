package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.user.UserRequest;
import com.softserve.itacademy.dto.user.UserResponse;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.RoleService;
import com.softserve.itacademy.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> readAll() {
        return new ResponseEntity<>(
                userService.getAll().stream()
                        .map(UserResponse::getUserResponse)
                        .collect(Collectors.toList())
                , HttpStatus.OK
        );
    }


    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> read(@PathVariable("id") long id) {
        return new ResponseEntity<>(UserResponse.getUserResponse(userService.readById(id)), HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setRole(roleService.readById(2L));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userService.create(user).getId())
                .toUri();
        return ResponseEntity.created(location).body(UserResponse.getUserResponse(user));
    }


    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") long id, @RequestBody UserRequest userRequest) {
        User user = userService.readById(id);
        user.setEmail(userRequest.getEmail() != null ? userRequest.getEmail() : user.getEmail());
        user.setPassword(userRequest.getPassword() != null ? userRequest.getPassword() : user.getPassword());
        user.setFirstName(userRequest.getFirstName() != null ? userRequest.getFirstName() : user.getFirstName());
        user.setLastName(userRequest.getLastName() != null ? userRequest.getLastName() : user.getLastName());
        user.setRole(roleService.getAll().stream()
                .filter(r->r.getName().equals(userRequest.getRole()))
                .findFirst()
                .orElse(user.getRole())
        );
        userService.update(user);
        return new ResponseEntity<>("User info updated successfully", HttpStatus.OK);
    }


    @PreAuthorize("hasAuthority('ADMIN') or authentication.principal.id == #id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        userService.delete(id);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }

}
