package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.common.StandardCRUDRepository;
import ru.practicum.shareit.user.User;

import java.util.Optional;

public interface UserRepository extends StandardCRUDRepository<User> {
    Optional<User> findByEmail(String email);
}
