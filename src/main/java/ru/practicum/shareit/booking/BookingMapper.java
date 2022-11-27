package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.user.UserMapper;

@Component
public class BookingMapper {
    public static BookingDto toBookingDto (Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDtoShort(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking (BookingDtoCreate bookingDtoCreate) {
        return Booking.builder()
                .item(Item.builder().id(bookingDtoCreate.getItemId()).build())
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .build();
    }

    public static BookingDtoShort toBookingDtoShort (Booking booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
