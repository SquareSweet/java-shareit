package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoCreate {
    private Long itemId;

    @NotNull(message = "Booking start date can not be empty")
    @FutureOrPresent(message = "Booking start date can not be in the past")
    private LocalDateTime start;

    @NotNull(message = "Booking start date can not be empty")
    @Future(message = "Booking end date should be in the future")
    private LocalDateTime end;
}
