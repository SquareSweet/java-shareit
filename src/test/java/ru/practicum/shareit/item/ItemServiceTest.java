package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.common.exceptions.ItemNotFoundException;
import ru.practicum.shareit.common.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.common.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceTest {
    private User user1;
    private User user2;
    private Item item1;
    private Item item2;
    private Item item3;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

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
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("Item2")
                .description("Item2 interesting description")
                .available(true)
                .build();
        item3 = Item.builder()
                .id(3L)
                .name("Item3")
                .description("Unavailable interesting item")
                .available(false)
                .build();
        userService.create(user1);
        userService.create(user2);
    }

    @Test
    void createTest() {
        itemService.create(item1, user1.getId());
        ItemDtoOut actualItem = itemService.findById(1L, user2.getId());
        assertEquals(ItemMapper.toItemDtoOut(item1), actualItem);
    }

    @Test
    void createInvalidUser() {
        assertThrows(UserNotFoundException.class, () -> itemService.create(item1, -1L));
    }

    @Test
    void updatePartial() {
        itemService.create(item1, user1.getId());
        Item itemUpdated = Item.builder()
                .id(1L)
                .description("updated description")
                .build();
        itemService.update(itemUpdated, user1.getId());
        assertEquals(item1.getName(), itemService.findById(1L, user1.getId()).getName());
        assertEquals(itemUpdated.getDescription(), itemService.findById(1L, user1.getId()).getDescription());
    }

    @Test
    void updateNotExisting() {
        assertThrows(ItemNotFoundException.class, () -> itemService.update(item1, 1L));
    }

    @Test
    void updateInvalidUser() {
        itemService.create(item1, user1.getId());
        assertThrows(UserNotFoundException.class, () -> itemService.update(item1, -1L));
    }

    @Test
    void updateNotOwner() {
        itemService.create(item1, user1.getId());
        assertThrows(UserIsNotOwnerException.class, () -> itemService.update(item1, 2L));
    }

    @Test
    void findByTestEmpty() {
        itemService.create(item1, user1.getId());
        itemService.create(item2, user2.getId());
        itemService.create(item3, user2.getId());
        assertEquals(List.of(), itemService.findByText(""));
    }
}