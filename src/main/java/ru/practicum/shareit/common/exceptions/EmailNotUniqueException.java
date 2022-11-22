package ru.practicum.shareit.common.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailNotUniqueException extends RuntimeException {
    public EmailNotUniqueException(String email) {
        super("User with this email already exists: " + email);
        log.warn("User with this email already exists: {}", email);
    }
}
