package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemToRequestDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ItemRequestDto {
    private int id;
    @NotBlank
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
}
