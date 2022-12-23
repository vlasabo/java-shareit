package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class ItemToRequestDto {
    private Integer id;
    private String name;
    private Integer ownerId;
    private String description;
    private Boolean available;
    private Integer requestId;

    public ItemToRequestDto(Integer id, String name, Integer ownerId, String description, Boolean available, Integer requestId) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.available = available;
        this.requestId = requestId;
    }

}
