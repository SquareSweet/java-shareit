package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequest request = ItemRequestMapper.toItemRequest(itemRequestDto);
        return ItemRequestMapper.toItemRequestDto(requestService.create(request, userId));
    }

    @GetMapping
    public List<ItemRequestDto> findByRequester(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findByUser(userId).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(@PathVariable Long requestId,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemRequestMapper.toItemRequestDto(requestService.findById(requestId, userId));
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam Optional<Integer> from,
                                        @RequestParam Optional<Integer> size) {
        return requestService.findAll(userId, from.orElse(0), size.orElse(20)).stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }
}
