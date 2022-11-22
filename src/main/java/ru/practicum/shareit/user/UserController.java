package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody User user) throws ValidationException {
        return userService.update(user);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.debug("get all users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserDto findUserById(@PathVariable Integer id) {
        log.debug("get user by id {}", id);
        return userService.findUserDtoById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Integer id) {
        log.debug("delete user by id {}", id);
        userService.deleteUserById(id);
    }

    @PostMapping()
    public UserDto addUser(@Valid @RequestBody User user) throws ValidationException {
        log.debug("add user {}", user);
        return userService.save(user);
    }

    @PatchMapping("/{id}")
    public UserDto updateUserField(@RequestBody User user, @PathVariable Integer id)
            throws IllegalAccessException, NoSuchFieldException, ValidationException {
        log.debug("patch user {}", user);
        return userService.updateFields(id, user);
    }
}
