package ru.practicum.shareit.item.dto;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CommentDto {
    @NotNull
    @NotBlank
    String text;
}
