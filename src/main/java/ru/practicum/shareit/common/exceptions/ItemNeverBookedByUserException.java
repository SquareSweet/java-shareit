package ru.practicum.shareit.common.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNeverBookedByUserException extends RuntimeException {
    public ItemNeverBookedByUserException(Long userId, Long itemId) {
        super("User id: " + userId + " has never booked item id: " + itemId);
        log.warn("User id: {} has never booked item id: {}", userId, itemId);
    }
}
