package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping()
    public ResponseEntity<Object> addUser(@Valid @RequestBody UserDto user) {
        log.debug("add user {}", user);
        return userClient.addUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Integer id) {
        log.debug("delete user by id {}", id);
        return userClient.deleteUserById(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.debug("get all users");
        return userClient.getAllUsers();
    }

    @PutMapping
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserDto user) {
        return userClient.update(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findUserById(@PathVariable Integer id) {
        log.debug("get user by id {}", id);
        return userClient.findUserDtoById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserField(@RequestBody UserDto userDto,
                                   @PathVariable Integer id) {
        log.debug("patch user {}", userDto);
        return userClient.updateFields(id, userDto);
    }
}
