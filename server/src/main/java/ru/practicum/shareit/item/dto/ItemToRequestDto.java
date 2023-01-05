package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


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

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * Objects.hash(name, id, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemToRequestDto itemToRequestDto = (ItemToRequestDto) o;
        return Objects.equals(id, itemToRequestDto.id) &&
                Objects.equals(name, itemToRequestDto.name) &&
                Objects.equals(description, itemToRequestDto.description);
    }

}
