package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.common.exceptions.*;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {
    private User user1;
    private Item item1;
    private Item item2;

    @Autowired
    private ItemService itemService;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private ItemRepository mockItemRepository;

    @MockBean
    private CommentRepository mockCommentRepository;

    @MockBean
    private BookingRepository mockBookingRepository;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).build();
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
                .description("Unavailable item")
                .available(false)
                .owner(user1)
                .build();
        Mockito.when(mockUserService.findById(1L)).thenReturn(user1);
        Mockito.when(mockItemRepository.save(item1)).thenReturn(item1);
        Mockito.when(mockItemRepository.findById(1L)).thenReturn(Optional.of(item1));
        Mockito.when(mockItemRepository.findById(2L)).thenReturn(Optional.of(item2));
        Mockito.when(mockUserService.findById(-1L)).thenThrow(UserNotFoundException.class);
    }

    @Test
    void createTest() {
        assertEquals(item1, itemService.create(item1, 1L));
        Mockito.verify(mockItemRepository, Mockito.times(1)).save(item1);
    }

    @Test
    void createInvalidUser() {
        assertThrows(UserNotFoundException.class, () -> itemService.create(item1, -1L));
    }

    @Test
    void findTest() {
        assertEquals(ItemMapper.toItemDtoOut(item1), itemService.findById(1L, 1L));
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void findNonExistingTest() {
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(-1L, 1L));
    }

    @Test
    void updateAllFieldsTest() {
        Item itemUpdated = Item.builder()
                .id(1L)
                .name("updated name")
                .description("updated description")
                .owner(user1)
                .available(false)
                .build();
        assertEquals(itemUpdated, itemService.update(itemUpdated, 1L));
        Mockito.verify(mockItemRepository, Mockito.times(1)).save(itemUpdated);
    }

    @Test
    void updatePartialTest() {
        Item itemUpdated = Item.builder()
                .id(1L)
                .description("updated description")
                .build();
        Item itemReturned = itemService.update(itemUpdated, 1L);
        assertEquals(item1.getName(), itemReturned.getName());
        assertEquals(itemUpdated.getDescription(), itemReturned.getDescription());
        assertEquals(item1.getAvailable(), itemReturned.getAvailable());
        Mockito.verify(mockItemRepository, Mockito.times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void updateNotExistingTest() {
        assertThrows(ItemNotFoundException.class, () -> itemService.update(Item.builder().id(-1L).build(), 1L));
    }

    @Test
    void updateInvalidUserTest() {
        assertThrows(UserNotFoundException.class, () -> itemService.update(item1, -1L));
    }

    @Test
    void updateNotOwnerTest() {
        assertThrows(UserIsNotOwnerException.class, () -> itemService.update(item1, 2L));
    }

    @Test
    void findByOwnerTest() {
        Mockito.when(mockItemRepository.findByOwnerId(1L, OffsetPageRequest.of(0, 20, Sort.by("id"))))
                .thenReturn(List.of(item1));
        assertEquals(List.of(ItemMapper.toItemDtoOut(item1)), itemService.findByOwner(1L, 0, 20));
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findByOwnerId(1L, OffsetPageRequest.of(0, 20, Sort.by("id")));
    }

    @Test
    void findForBookingByIdTest() {
        assertEquals(item1, itemService.findForBookingById(1L, 2L));
        Mockito.verify(mockItemRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    void findForBookingByIdOwnerTest() {
        assertThrows(ItemNotFoundException.class, () -> itemService.findForBookingById(1L, 1L));
    }

    @Test
    void findForBookingByIdNotAvailableTest() {
        assertThrows(ItemNotAvailableException.class, () -> itemService.findForBookingById(2L, 2L));
    }

    @Test
    void findByTextTest() {
        Mockito.when(mockItemRepository.findByText("Item1", OffsetPageRequest.of(0, 20, Sort.by("id"))))
                .thenReturn(List.of(item2));
        assertEquals(List.of(item2), itemService.findByText("Item1", 0, 20));
        Mockito.verify(mockItemRepository, Mockito.times(1))
                .findByText("Item1", OffsetPageRequest.of(0, 20, Sort.by("id")));
    }

    @Test
    void findByTextEmptyTest() {
        assertEquals(List.of(), itemService.findByText("", 0, 20));
        Mockito.verify(mockItemRepository, Mockito.never()).findByText(Mockito.anyString(), Mockito.any(OffsetPageRequest.class));
    }

    @Test
    void deleteTest() {
        itemService.deleteById(1L);
        Mockito.verify(mockItemRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    void addCommentTest() {
        Mockito.when(mockBookingRepository.findByItemIdAndStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class)))
                .thenReturn(List.of(Booking.builder()
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now())
                        .item(item1)
                        .booker(user1)
                        .status(BookingStatus.APPROVED)
                        .build()));
        Comment comment = Comment.builder()
                .id(1L)
                .item(item1)
                .author(user1)
                .text("test comment")
                .build();
        Mockito.when(mockCommentRepository.save(comment)).thenReturn(comment);
        assertEquals(comment, itemService.addComment(1L, 1L,comment));
        Mockito.verify(mockCommentRepository, Mockito.times(1)).save(comment);
    }

    @Test
    void addCommentNotBookedTest() {
        Comment comment = Comment.builder()
                .id(1L)
                .item(item1)
                .author(user1)
                .text("test comment")
                .build();
        assertThrows(ItemNeverBookedByUserException.class, () -> itemService.addComment(1L, 2L, comment));
    }
}