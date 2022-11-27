package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
            log.error("Booking creation fail: start date is after end date");
            throw new ValidationException("Booking start date is after end date");
        }
    }

    public Booking findById(Long id, Long userId) {
        userService.findById(userId); //throws exception if user does not exist
        return bookingRepository.findByIdAndOwnerOrBooker(id, userId)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    public List<Booking> findByBooker(Long userId, String status) {
        userService.findById(userId); //throws exception if user does not exist
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            switch (bookingStatus) {
                case ALL:
                    return bookingRepository.findByBookerIdOrderByStartDesc(userId);
                case WAITING:
                    return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                case REJECTED:
                    return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                case CURRENT:
                    return bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now());
                case PAST:
                    return bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                case FUTURE:
                    return bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                default:
                    log.error("Unknown booking status: {} when finding by booker", status);
                    throw new IllegalArgumentException("Unknown booking status: " + status);
            }
        } catch (IllegalArgumentException e) {
            log.error("Unknown booking status: {} when finding by owner", status);
            throw new IllegalArgumentException("Unknown state: " + status);
        }
    }

    public List<Booking> findByOwner(Long userId, String status) {
        userService.findById(userId); //throws exception if user does not exist
        try {
            BookingStatus bookingStatus = BookingStatus.valueOf(status.toUpperCase());
            switch (bookingStatus) {
                case ALL:
                    return bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                case WAITING:
                    return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                case REJECTED:
                    return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                case CURRENT:
                    return bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                            userId,
                            LocalDateTime.now(),
                            LocalDateTime.now());
                case PAST:
                    return bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                case FUTURE:
                    return bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                default:
                    throw new IllegalArgumentException("Unknown state: " + status);
            }
        } catch (IllegalArgumentException e) {
            log.error("Unknown booking status: {} when finding by owner", status);
            throw new IllegalArgumentException("Unknown state: " + status);
        }
    }

    public Booking updateStatus(Long id, Long ownerId, Boolean isApproved) {
        Booking booking = bookingRepository.findByIdAndItemOwnerId(id, ownerId)
                .orElseThrow(() -> new BookingNotFoundException(id));
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Booking id: {} is already approved", id);
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
