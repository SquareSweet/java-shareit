package ru.practicum.shareit.common.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Booking id does not exist: " + id);
        log.warn("Booking id does not exist: {}", id);
    }
}
