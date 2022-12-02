package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.common.exceptions.BookingNotFoundException;
import ru.practicum.shareit.common.exceptions.UserIsNotOwnerException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking create(Booking booking, Long userId) {
        if (booking.getStart() != null && booking.getStart().isBefore(booking.getEnd())) {
            booking.setBooker(userService.findById(userId));
            booking.setItem(itemService.findForBookingById(booking.getItem().getId(), userId));
            booking.setStatus(BookingStatus.WAITING);
            booking = bookingRepository.save(booking);
            log.info("Create item id: {}", booking.getId());
            return booking;
        } else {
            throw new ValidationException("Booking start date is after end date");
        }
    }

    public Booking findById(Long id, Long userId) {
        userService.findById(userId); //throws exception if user does not exist
        return bookingRepository.findByIdAndOwnerOrBooker(id, userId)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    public List<Booking> findByBooker(Long userId, String status, int from, int size) {
        userService.findById(userId); //throws exception if user does not exist
        Pageable pageable = OffsetPageRequest.of(from, size, Sort.by("start").descending());
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            switch (bookingStatus) {
                case ALL:
                    return bookingRepository.findByBookerId(userId, pageable);
                case WAITING:
                    return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                case REJECTED:
                    return bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                case CURRENT:
                    return bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            pageable
                    );
                case PAST:
                    return bookingRepository.findByBookerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                case FUTURE:
                    return bookingRepository.findByBookerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                default:
                    throw new IllegalArgumentException("Unknown booking status: " + status);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + status);
        }
    }

    public List<Booking> findByOwner(Long userId, String status, int from, int size) {
        userService.findById(userId); //throws exception if user does not exist
        Pageable pageable = OffsetPageRequest.of(from, size, Sort.by("start").descending());
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            switch (bookingStatus) {
                case ALL:
                    return bookingRepository.findByItemOwnerId(userId, pageable);
                case WAITING:
                    return bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
                case REJECTED:
                    return bookingRepository.findByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
                case CURRENT:
                    return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            pageable
                    );
                case PAST:
                    return bookingRepository.findByItemOwnerIdAndEndBefore(userId, LocalDateTime.now(), pageable);
                case FUTURE:
                    return bookingRepository.findByItemOwnerIdAndStartAfter(userId, LocalDateTime.now(), pageable);
                default:
                    throw new IllegalArgumentException("Unknown state: " + status);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown state: " + status);
        }
    }

    public Booking updateStatus(Long id, Long ownerId, Boolean isApproved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(id, ownerId)
                .orElseThrow(() -> new BookingNotFoundException(id));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new IllegalArgumentException("Booking id: " + id + " is already approved");
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new UserIsNotOwnerException(ownerId, booking.getItem().getId());
        }
        booking.setStatus((isApproved) ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Update status booking id: {}", id);
        return bookingRepository.save(booking);
    }
}
