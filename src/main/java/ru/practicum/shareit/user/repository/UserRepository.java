package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    public User getUserById(int id);

    public List<User> getAllUsers();

    public void save(User user);
}
