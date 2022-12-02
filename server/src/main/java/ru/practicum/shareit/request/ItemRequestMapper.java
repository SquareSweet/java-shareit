package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .requester(request.getRequester())
                .created(request.getCreated())
                .items(request.getItems().stream()
                        .map(ItemMapper::toItemDtoRequest)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requester(requestDto.getRequester())
                .created(requestDto.getCreated())
                .build();
    }
}
