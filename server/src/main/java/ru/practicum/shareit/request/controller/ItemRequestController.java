package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                         @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("add item request {}", itemRequestDto);
        return itemRequestService.save(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("find all item requests by user {}", userId);
        return itemRequestService.getAllRequestsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @PathVariable Integer id) {
        log.debug("add all item requests N{}", id);
        return itemRequestService.getRequestById(id, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                       @RequestParam(value = "from") Integer offset,
                                       @RequestParam(value = "size") Integer size) {
        return itemRequestService.getAll(offset, size, userId);
    }
}
