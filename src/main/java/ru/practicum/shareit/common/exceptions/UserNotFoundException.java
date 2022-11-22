package ru.practicum.shareit.common.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super("User id does not exist: " + id);
        log.warn("User id does not exist: {}", id);
    }
}
