package ru.practicum.shareit.common.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserIsNotOwnerException extends RuntimeException {
    public UserIsNotOwnerException(Long userId, Long itemId) {
        super("User id: " + userId + " is nit owner of item id: " + itemId);
        log.warn("User id: {} is not owner of item id: {}", userId, itemId);
    }
}
