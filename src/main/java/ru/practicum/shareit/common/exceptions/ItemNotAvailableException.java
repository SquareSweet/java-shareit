package ru.practicum.shareit.common.exceptions;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(Long itemId) {
        super("Item id: " + itemId + " is not available");
    }
}
