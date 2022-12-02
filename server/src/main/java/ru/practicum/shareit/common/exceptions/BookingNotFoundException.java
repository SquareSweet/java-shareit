package ru.practicum.shareit.common.exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super("Booking id does not exist: " + id);
    }
}
