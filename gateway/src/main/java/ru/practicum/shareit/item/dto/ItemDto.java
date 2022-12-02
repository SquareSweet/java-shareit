package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    @NotBlank(message = "Item name should not be blank")
    String name;
    @NotBlank(message = "Item description should not be blank")
    String description;
    @NotNull(message = "Item availability should be specified")
    Boolean available;
    Long requestId;
}
