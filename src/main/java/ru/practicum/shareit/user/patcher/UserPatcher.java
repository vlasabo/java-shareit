package ru.practicum.shareit.user.patcher;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserPatcher {
    public static User patchUser(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }
}
