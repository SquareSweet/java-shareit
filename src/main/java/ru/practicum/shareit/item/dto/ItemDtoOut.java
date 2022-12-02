package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(exclude = "comments")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoOut {
    Long id;
    String name;
    String description;
    Boolean available;
    User owner;
    ItemRequest request;
    BookingDtoShort lastBooking;
    BookingDtoShort nextBooking;
    List<CommentDto> comments;
}
