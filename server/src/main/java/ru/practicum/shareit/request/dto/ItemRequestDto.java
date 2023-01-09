package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemToRequestDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestDto {
    private int id;
    private String description;
    private int userId;
    private LocalDateTime created;
    private List<ItemToRequestDto> items;

    public ItemRequestDto(int id, String description, int userId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.created = created;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * Objects.hash(id, description, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequestDto itemRequestDto = (ItemRequestDto) o;
        return id == itemRequestDto.id &&
                userId == itemRequestDto.userId &&
                Objects.equals(description, itemRequestDto.description);
    }
}
