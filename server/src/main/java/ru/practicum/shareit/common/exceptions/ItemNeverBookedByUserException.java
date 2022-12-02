package ru.practicum.shareit.common.exceptions;

public class ItemNeverBookedByUserException extends RuntimeException {
    public ItemNeverBookedByUserException(Long userId, Long itemId) {
        super("User id: " + userId + " has never booked item id: " + itemId);
    }
}
