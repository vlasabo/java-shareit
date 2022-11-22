package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepositoryInMemoryImpl implements UserRepository {
    private final Map<Integer, User> allUsers;
    private static int id;

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(allUsers.get(id));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User save(User user) {
        int id = (user.getId() == 0) ? getNextId() : user.getId();
        user.setId(id);
        allUsers.put(id, user);
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        allUsers.remove(id);
    }

    private static Integer getNextId() {
        return ++id;
    }
}
