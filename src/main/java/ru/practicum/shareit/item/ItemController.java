package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
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
    public ItemDtoBooking find(@PathVariable Long itemId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoBooking> findByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        return itemService.findByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        itemService.deleteById(itemId);
    }
}
