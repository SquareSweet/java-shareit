package ru.practicum.shareit.common;

import java.util.List;
import java.util.Optional;

public interface StandardCRUDRepository<T> {

    T save(T t);

    T update(T t);

    Optional<T> findById(Long id);

    List<T> findAll();

    void deleteById(Long id);

}
