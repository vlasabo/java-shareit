package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("get all items");
        return itemService.getAllItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto findItemById(@PathVariable Integer id) {
        log.debug("get item by id {}", id);
        return itemService.findItemDtoById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemById(@RequestParam String text) {
        log.debug("find item by request {}", text);
        return itemService.findItemDtoByDescOrName(text);
    }

    @PostMapping
    public Item addItem(@Valid @RequestBody ItemDto itemDto,
                        @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("add item {}", itemDto);
        return itemService.save(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateUserField(@RequestBody ItemDto itemDto,
                                   @RequestHeader("X-Sharer-User-Id") int userId,
                                   @PathVariable Integer id) throws NoSuchFieldException, IllegalAccessException {
        log.debug("patch item {}", itemDto);
        return itemService.updateFields(id, itemDto, userId);
    }
}
