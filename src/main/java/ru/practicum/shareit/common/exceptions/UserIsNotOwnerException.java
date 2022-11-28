package ru.practicum.shareit.common.exceptions;

public class UserIsNotOwnerException extends RuntimeException {
    public UserIsNotOwnerException(Long userId, Long itemId) {
        super("User id: " + userId + " is nit owner of item id: " + itemId);
    }
}
