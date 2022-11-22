package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.webjars.NotFoundException;
import ru.practicum.shareit.item.controller.handler.ErrorResponse;
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
    public List<ItemDto> getAllItems() {
        log.debug("get all items");
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ItemDto findItemById(@PathVariable Integer id) {
        log.debug("get item by id {}", id);
        return itemService.findItemDtoById(id);
    }

    @PostMapping
    public Item addItem(@Valid @RequestBody Item item,
                        @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("add item {}", item);
        return itemService.save(item, userId);
    }


    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final MethodArgumentNotValidException e) {
        return new ErrorResponse(
                "Data validation error", e.getMessage()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(final NotFoundException e) {
        return new ErrorResponse(
                "No data found", e.getMessage()
        );
    }


}
