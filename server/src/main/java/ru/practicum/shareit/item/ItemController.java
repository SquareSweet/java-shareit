package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        return ItemMapper.toItemDto(itemService.create(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemService.update(item, userId));
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut find(@PathVariable Long itemId,
                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoOut> findByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam Optional<Integer> from,
                                        @RequestParam Optional<Integer> size) {
        return itemService.findByOwner(userId, from.orElse(0), size.orElse(20));
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text,
                              @RequestParam Optional<Integer> from,
                              @RequestParam Optional<Integer> size) {
        return itemService.findByText(text, from.orElse(0), size.orElse(20)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable("itemId") long itemId,
                                 @RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        Comment comment = CommentMapper.toComment(commentDto);
        return CommentMapper.toCommentDto(itemService.addComment(itemId, userId, comment));
    }
}
