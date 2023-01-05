package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotFoundException extends RuntimeException{
    private final String message;
}
