package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {
    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@Valid @RequestBody RequestDto itemRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("add item request {}", itemRequestDto);
        return requestClient.save(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("find all item requests by user {}", userId);
        return requestClient.getAllRequestsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                                 @PathVariable Integer id) {
        log.debug("add all item requests N{}", id);
        return requestClient.getRequestById(id, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer offset,
                                         @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return requestClient.getAll(offset, size, userId);
    }
}
