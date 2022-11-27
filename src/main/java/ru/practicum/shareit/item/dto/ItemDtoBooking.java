package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.user.User;

@Data
@Builder
public class ItemDtoBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
}
