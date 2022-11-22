package ru.practicum.shareit.item.controller.handler;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
    private final String description;
}
