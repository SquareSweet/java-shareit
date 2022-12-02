package ru.practicum.shareit.common.exceptions;

public class ItemRequestNotFound extends RuntimeException {
    public ItemRequestNotFound(Long id) {
        super("Item request id does not exist: " + id);
    }
}
