package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;

    @NotBlank(message = "Item name should not be blank")
    private String name;

    @NotBlank(message = "Item description should not be blank")
    private String description;

    @NotNull(message = "Item availability should be specified")
    private Boolean available;

    private User owner;

    private ItemRequest request;
}
