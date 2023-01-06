package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UserIntegrationTests {
    @Autowired
    UserService userService;

    User user1;
    User user2;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setName("name");
        user1.setId(1);
        user1.setEmail("email1@mail.com");
        userService.save(user1);
        user2 = new User();
        user2.setName("name2");
        user2.setId(2);
        user2.setEmail("email2@mail.com");
    }

    @Test
    void save() {
        Assertions.assertEquals(userService.save(user2), UserMapper.toUserDto(user2));
    }

    @Test
    void getAllUsers() {
        userService.save(user2);
        Assertions.assertEquals(userService.getAllUsers().size(), 2);
    }

    @Test
    void findUserDtoById() {
        Assertions.assertEquals(userService.findUserDtoById(1), UserMapper.toUserDto(user1));
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserDtoById(10));
    }

    @Test
    void findUserById() {
        Assertions.assertEquals(userService.findUserById(1), Optional.of(user1));
        Assertions.assertEquals(userService.findUserById(10), Optional.empty());
    }

    @Test
    void update() {
        userService.save(user2);
        user2.setEmail("blavla@mail.com");
        Assertions.assertEquals(userService.update(user2), UserMapper.toUserDto(user2));
    }

    @Test
    void updateFields() {
        userService.save(user2);
        user2.setEmail("blavla@mail.com");
        Assertions.assertEquals(userService.updateFields(2, UserMapper.toUserDto(user2)), UserMapper.toUserDto(user2));
    }

    @Test
    void findAllUsersInList() {
        userService.save(user2);
        Assertions.assertEquals(userService.findAllUsersInList(List.of(1, 2)).size(), 2);
    }
}
