package ru.practicum.shareit.exception;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CustomValidateException extends RuntimeException {
    private final String message;
}
