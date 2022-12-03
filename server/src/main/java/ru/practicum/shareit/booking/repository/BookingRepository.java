package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("select b from Booking b join b.item i " +
            "where b.id = ?1 and (i.owner.id = ?2 or b.booker.id = ?2)")
    Optional<Booking> findByIdAndOwnerOrBooker(Long id, Long userId);

    Optional<Booking> findByIdAndItemOwnerId(Long is, Long ownerId);

    List<Booking> findByItemIdAndStatus(Long itemId,BookingStatus status);

    List<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime startAfter, Pageable pageable);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime endBefore, Pageable pageable);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(
            Long bookerId,
            LocalDateTime startBefore,
            LocalDateTime endAfter,
            Pageable pageable
    );

    List<Booking> findByItemOwnerId(Long ownerId, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime startAfter, Pageable pageable);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime endBefore, Pageable pageable);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(
            Long ownerId,
            LocalDateTime startBefore,
            LocalDateTime endAfter,
            Pageable pageable
    );
}
