package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

@AllArgsConstructor
@Getter
@Setter
public class UserDto extends User {
    private int id;
    private String name;
    private String email;
}
