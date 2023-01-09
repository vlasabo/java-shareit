package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class RequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                itemRequest.getUserId(), itemRequest.getCreated());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(),
                itemRequestDto.getUserId(), itemRequestDto.getCreated());
    }
}
