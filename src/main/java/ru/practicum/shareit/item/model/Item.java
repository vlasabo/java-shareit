package ru.practicum.shareit.item.model;

import lombok.Getter;
import lombok.Setter;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Getter
@Setter
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest itemRequest;
}
