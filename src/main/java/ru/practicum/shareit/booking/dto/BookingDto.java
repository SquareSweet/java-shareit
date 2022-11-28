package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "Booking start date can not be empty")
    @FutureOrPresent(message = "Booking start date can not be in the past")
    private LocalDateTime start;

    @NotNull(message = "Booking start date can not be empty")
    @Future(message = "Booking end date should be in the future")
    private LocalDateTime end;

    private ItemDtoShort item;

    private UserDto booker;

    private BookingStatus status;
}
