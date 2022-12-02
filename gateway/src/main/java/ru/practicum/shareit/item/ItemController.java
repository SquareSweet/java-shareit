package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> createItem(@Valid @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Updating item {}, userId={}", itemId, userId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findItemByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Get items by owner {}, from={}, size{}", userId, from, size);
        return itemClient.findItemByOwner(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam String text,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "20") Integer size) {
        log.info("Get items by text '{}', userId={}, from={}, size{}", text, userId, from, size);
        return itemClient.findItemByText(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long itemId) {
        log.info("Deleting item {}", itemId);
        return itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable("itemId") long itemId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Creating comment '{}', itemId={}, userId={}", commentDto, itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
