package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.common.StandardCRUDRepository;
import ru.practicum.shareit.item.Item;

import java.util.List;

public interface ItemRepository extends StandardCRUDRepository<Item> {

    List<Item> findByOwner(Long userId);

    List<Item> findByText(String text);

}
