package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;

    @NotBlank(message = "Item name should not be blank")
    String name;

    @NotBlank(message = "Item description should not be blank")
    String description;

    @NotNull(message = "Item availability should be specified")
    Boolean available;

    User owner;

    Long requestId;
}
