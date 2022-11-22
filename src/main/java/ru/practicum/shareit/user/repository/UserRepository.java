package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    public Optional<User> getUserById(int id);

    public List<User> getAllUsers();

    public User save(User user);

    void deleteUserById(int id);
}
