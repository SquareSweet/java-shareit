package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private final Map<Long, Item> items;
    private long nextId;

    ItemRepositoryInMemoryImpl() {
        items = new HashMap<>();
        nextId = 1;
    }

    private long getNextId() {
        return nextId++;
    }

    @Override
    public Item save(Item item) {
        long id = getNextId();
        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long id) {
        if (items.containsKey(id)) {
            return Optional.of(items.get(id));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> findByOwner(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> findByText(String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseText)
                        || item.getDescription().toLowerCase().contains(lowerCaseText))
                .collect(Collectors.toList());
    }
}
