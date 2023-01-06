package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") int userId,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer offset,
                                              @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        log.debug("get all items");
        return itemClient.getAllItems(userId, offset, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findItemById(@PathVariable Integer id,
                                               @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("get item by id {}", id);
        return itemClient.findItemDtoById(id, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemById(@RequestParam String text) {
        log.debug("find item by request {}", text);
        return itemClient.findItemDtoByDescOrName(text);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.debug("add item {}", itemDto);
        return itemClient.save(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUserField(@RequestBody ItemDto itemDto,
                                                  @RequestHeader("X-Sharer-User-Id") int userId,
                                                  @PathVariable Integer id) {
        log.debug("patch item {}", itemDto);
        return itemClient.updateFields(id, itemDto, userId);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody CommentDto comment,
                                             @RequestHeader("X-Sharer-User-Id") int userId,
                                             @PathVariable Integer itemId) {
        return itemClient.addComment(userId, itemId, comment);
    }

}