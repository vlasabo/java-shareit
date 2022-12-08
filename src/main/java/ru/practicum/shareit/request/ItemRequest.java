package ru.practicum.shareit.request;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Transient;
import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequest {
    private int id;
    private String description;
    @Transient
    private User requestor;
    private int userId;
    private LocalDateTime created;
}
