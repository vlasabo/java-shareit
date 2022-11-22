package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class User {
    private int id;
    private String name;
    @Email(message = "EMAIL IS INCORRECT")
    @NotEmpty
    private String email;
}
