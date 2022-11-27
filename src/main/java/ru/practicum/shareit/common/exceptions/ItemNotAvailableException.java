package ru.practicum.shareit.common.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(Long itemId) {
        super("Item id: " + itemId + " is not available");
        log.warn("User id: {} is not available", itemId);
    }
}
