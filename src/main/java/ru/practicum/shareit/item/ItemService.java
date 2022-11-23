package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exceptions.ItemNotFoundException;
import ru.practicum.shareit.common.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public Item create(Item item, Long userId) {
        User owner = userService.findById(userId); //throws exception if user does not exist
        item.setOwner(owner);
        item.setAvailable(true);
        log.info("Create item id: {}", item.getId());
        return itemRepository.save(item);
    }

    public Item update(Item item, Long userId) {
        userService.findById(userId); //throws exception if user does not exist
        Item existingItem = itemRepository.findById(item.getId())
                .orElseThrow(() -> new ItemNotFoundException(item.getId()));
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new UserIsNotOwnerException(userId, item.getId());
        }
        if (item.getName() != null) {
            existingItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            existingItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            existingItem.setAvailable(item.getAvailable());
        }

        log.info("Update item id: {}", item.getId());
        return itemRepository.update(existingItem);
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    public List<Item> findByOwner(Long userId) {
        return itemRepository.findByOwner(userId);
    }

    public List<Item> findByText(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByText(text);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
        log.info("Delete item id: {}", id);
    }
}
