package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Data
@Builder
@EqualsAndHashCode(exclude = "comments")
public class ItemDtoOut {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
}
