package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Sql(scripts = {"classpath:schema.sql", "classpath:data_ItemRepositoryTest.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTextInNameTest() {
        List<Item> expectedItems = List.of(Item.builder()
                .id(1L)
                .name("testItem1_nameKeyword")
                .description("testDescr1")
                .owner(userRepository.getReferenceById(1L))
                .available(true)
                .build());
        List<Item> items = itemRepository.findByText("nameKeyword", OffsetPageRequest.of(0, 20));
        assertEquals(1, items.size());
        assertEquals(expectedItems, items);
    }

    @Test
    void findByTextInDescriptionTest() {
        List<Item> expectedItems = List.of(Item.builder()
                .id(3L)
                .name("testItem3")
                .description("testDescr3_descriptionKeyword")
                .owner(userRepository.getReferenceById(1L))
                .available(true)
                .build());
        List<Item> items = itemRepository.findByText("descriptionKeyword", OffsetPageRequest.of(0, 20));
        assertEquals(1, items.size());
        assertEquals(expectedItems, items);
    }

    @Test
    void findByTextInNameAndDescriptionTest() {
        List<Item> expectedItems = List.of(
                Item.builder()
                        .id(1L)
                        .name("testItem1_nameKeyword")
                        .description("testDescr1")
                        .owner(userRepository.getReferenceById(1L))
                        .available(true)
                        .build(),
                Item.builder()
                        .id(3L)
                        .name("testItem3")
                        .description("testDescr3_descriptionKeyword")
                        .owner(userRepository.getReferenceById(1L))
                        .available(true)
                        .build());
        List<Item> items = itemRepository.findByText("keyword", OffsetPageRequest.of(0, 20));
        assertEquals(2, items.size());
        assertEquals(expectedItems, items);
    }
}