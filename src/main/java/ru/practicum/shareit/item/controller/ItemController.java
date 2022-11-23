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
import javax.validation.ValidationException;
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

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(final ValidationException e) {
        return new ErrorResponse(
                "Incorrect validation", e.getMessage()
        );
    }
}