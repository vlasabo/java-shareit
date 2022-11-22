package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private int id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest itemRequest;

    public Item(String name, String description, boolean available, int id) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.id = id;
    }
}
