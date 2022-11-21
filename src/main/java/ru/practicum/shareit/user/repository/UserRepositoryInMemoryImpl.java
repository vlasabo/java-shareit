package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UserRepositoryInMemoryImpl implements UserRepository{
    private final Map<Integer, User> allUsers;

    @Override
    public User getUserById(int id) {
        return allUsers.getOrDefault(id, null);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public void save(User user) {
        int id = (user.getId() == 0) ? allUsers.size() + 1 : user.getId();
        user.setId(id);
        allUsers.put(id, user);
    }
}
