package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.common.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRepositoryInMemoryImplTest {
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(1L)
                .email("user1@user.com")
                .name("user1")
                .build();
        user2 = User.builder()
                .id(2L)
                .email("user2@user.com")
                .name("user2")
                .build();
        item1 = Item.builder()
                .id(1L)
                .name("Item1")
                .description("Item1 description")
                .available(true)
                .owner(user1)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 interesting description")
                .available(true)
                .owner(user2)
                .build();
        item3 = Item.builder()
                .id(3L)
                .name("Item3")
                .description("Unavailable interesting item")
                .available(false)
                .owner(user2)
                .build();
    }

    @Test
    void createTest() {
        itemRepository.save(item1);
        assertEquals(1, itemRepository.findAll().size());
        assertEquals(Optional.of(item1), itemRepository.findById(1L));
    }

    @Test
    void updateTest() {
        itemRepository.save(item1);
        item1.setName("updated item");
        item1.setDescription("updated item description");
        itemRepository.update(item1);
        assertEquals(1, itemRepository.findAll().size());
        assertEquals(Optional.of(item1), itemRepository.findById(1L));
    }

    @Test
    void deleteTest() {
        itemRepository.save(item1);
        itemRepository.save(item2);
        assertEquals(2, itemRepository.findAll().size());
        itemRepository.deleteById(1L);
        assertEquals(1, itemRepository.findAll().size());
        assertEquals(List.of(item2), itemRepository.findAll());
    }

    @Test
    void deleteNonExistingTest() {
        assertThrows(ItemNotFoundException.class, () -> itemRepository.deleteById(-1L));
    }

    @Test
    void findByOwnerTest() {
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        assertEquals(2, itemRepository.findByOwner(2L).size());
        assertEquals(List.of(item2, item3), itemRepository.findByOwner(2L));
    }

    @Test
    void findByTextTest() {
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        assertEquals(1, itemRepository.findByText("interesting").size());
        assertEquals(List.of(item2), itemRepository.findByText("interesting"));
    }
}