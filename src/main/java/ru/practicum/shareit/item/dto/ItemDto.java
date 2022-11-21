package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public class ItemDto implements Serializable {
    private String name;
    private String description;
    private boolean available;
    private int id;

}
