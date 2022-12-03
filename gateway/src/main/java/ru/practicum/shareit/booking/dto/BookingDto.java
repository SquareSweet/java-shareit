package ru.practicum.shareit.booking.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingDto {
    @NotNull
    Long itemId;
    @NotNull(message = "Booking start date can not be empty")
    @FutureOrPresent(message = "Booking start date can not be in the past")
    LocalDateTime start;
    @NotNull(message = "Booking start date can not be empty")
    @Future(message = "Booking end date should be in the future")
    LocalDateTime end;
}
