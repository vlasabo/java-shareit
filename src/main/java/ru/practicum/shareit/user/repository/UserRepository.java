package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(int id);

    List<User> getAllUsers();

    User save(User user);

    void deleteUserById(int id);
}
