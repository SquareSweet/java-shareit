package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.common.exceptions.BookingNotFoundException;
import ru.practicum.shareit.common.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
class BookingServiceTest {
    private Booking booking;
    private Item item;
    private User owner;
    private User booker;
    private int from;
    private int size;
    private Pageable pageable;

    @Autowired
    private BookingService bookingService;

    @MockBean
    private UserService mockUserService;

    @MockBean
    private ItemService mockItemService;

    @MockBean
    private BookingRepository mockRepository;

    @BeforeEach
    void setUp() {
        owner = User.builder().id(1L).build();
        booker = User.builder().id(2L).build();
        item = Item.builder()
                .id(1L)
                .owner(owner)
                .available(true)
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        Mockito.when(mockUserService.findById(1L)).thenReturn(owner);
        Mockito.when(mockUserService.findById(2L)).thenReturn(booker);
        Mockito.when(mockItemService.findForBookingById(1L, 2L)).thenReturn(item);
        Mockito.when(mockRepository.save(booking)).thenReturn(booking);
        Mockito.when(mockRepository.findById(1L)).thenReturn(Optional.of(booking));
        Mockito.when(mockRepository.findByIdAndOwnerOrBooker(1L, 1L)).thenReturn(Optional.of(booking));
        Mockito.when(mockRepository.findByIdAndItemOwnerId(eq(1L), Mockito.anyLong())).thenReturn(Optional.of(booking));

        from = 0;
        size = 20;
        pageable = OffsetPageRequest.of(from, size, Sort.by("start").descending());
    }

    @Test
    void createTest() {
        assertEquals(booking, bookingService.create(booking, 1L));
        Mockito.verify(mockRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void createWrongDatesTest() {
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().minusDays(1));
        assertThrows(ValidationException.class, () -> bookingService.create(booking, 1L));
    }

    @Test
    void findByIdTest() {
        assertEquals(booking, bookingService.findById(1L, 1L));
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByIdAndOwnerOrBooker(1L, 1L);
    }

    @Test
    void findNonExistingTest() {
        assertThrows(BookingNotFoundException.class, () -> bookingService.findById(-1L, 1L));
    }

    @Test
    void updateStatusApprovedTest() {
        bookingService.updateStatus(1L, 1L, true);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        Mockito.verify(mockRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void updateStatusRejectedTest() {
        bookingService.updateStatus(1L, 1L, false);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        Mockito.verify(mockRepository, Mockito.times(1)).save(booking);
    }

    @Test
    void updateStatusNotFound() {
        assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateStatus(2L, 1L, true));
    }

    @Test
    void updateStatusAlreadyApproved() {
        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.updateStatus(1L, 1L, true));
    }

    @Test
    void updateStatusNotOwner() {
        assertThrows(UserIsNotOwnerException.class,
                () -> bookingService.updateStatus(1L, 2L, true));
    }

    @Test
    void findAllByBookerTest() {
        bookingService.findByBooker(1L, "ALL", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByBookerId(1L, pageable);
    }

    @Test
    void findWaitingByBookerTest() {
        bookingService.findByBooker(1L, "WAITING", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByBookerIdAndStatus(1L, BookingStatus.WAITING, pageable);
    }

    @Test
    void findRejectedByBookerTest() {
        bookingService.findByBooker(1L, "REJECTED", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByBookerIdAndStatus(1L, BookingStatus.REJECTED, pageable);
    }

    @Test
    void findCurrentByBookerTest() {
        bookingService.findByBooker(1L, "CURRENT", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByBookerIdAndStartBeforeAndEndAfter(eq(1L),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void findPastByBookerTest() {
        bookingService.findByBooker(1L, "PAST", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByBookerIdAndEndBefore(eq(1L), Mockito.any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void findFutureByBookerTest() {
        bookingService.findByBooker(1L, "FUTURE", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByBookerIdAndStartAfter(eq(1L), Mockito.any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void findUnknownByBookerTest() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findByBooker(1L, "unknown", from, size));
    }

    @Test
    void findAllByOwnerTest() {
        bookingService.findByOwner(1L, "ALL", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByItemOwnerId(1L, pageable);
    }

    @Test
    void findWaitingByOwnerTest() {
        bookingService.findByOwner(1L, "WAITING", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByItemOwnerIdAndStatus(1L, BookingStatus.WAITING, pageable);
    }

    @Test
    void findRejectedByOwnerTest() {
        bookingService.findByOwner(1L, "REJECTED", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByItemOwnerIdAndStatus(1L, BookingStatus.REJECTED, pageable);
    }

    @Test
    void findCurrentByOwnerTest() {
        bookingService.findByOwner(1L, "CURRENT", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByItemOwnerIdAndStartBeforeAndEndAfter(eq(1L),
                        Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void findPastByOwnerTest() {
        bookingService.findByOwner(1L, "PAST", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByItemOwnerIdAndEndBefore(eq(1L), Mockito.any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void findFutureByOwnerTest() {
        bookingService.findByOwner(1L, "FUTURE", from, size);
        Mockito.verify(mockRepository, Mockito.times(1))
                .findByItemOwnerIdAndStartAfter(eq(1L), Mockito.any(LocalDateTime.class), eq(pageable));
    }

    @Test
    void findUnknownByOwnerTest() {
        assertThrows(IllegalArgumentException.class,
                () -> bookingService.findByOwner(1L, "unknown", from, size));
    }
}