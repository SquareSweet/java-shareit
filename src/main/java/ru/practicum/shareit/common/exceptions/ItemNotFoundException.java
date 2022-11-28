package ru.practicum.shareit.common.exceptions;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super("Item id does not exist: " + id);
    }
}
