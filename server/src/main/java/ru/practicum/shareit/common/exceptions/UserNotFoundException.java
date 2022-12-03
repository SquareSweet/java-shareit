package ru.practicum.shareit.common.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User id does not exist: " + id);
    }
}
