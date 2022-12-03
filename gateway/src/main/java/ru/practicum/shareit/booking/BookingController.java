package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDto bookingDto,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Booking end date should be after start date");
        }
        log.info("Creating booking {}, userId={}", bookingDto, userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @GetMapping("/{bookingId}")
    ResponseEntity<Object> find(@PathVariable Long bookingId,
                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.findBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> findBookingByBooker(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingByBooker(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "20") Integer size) {

        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.findBookingByOwner(userId, state, from, size);
    }

    @PatchMapping(path = "/{bookingId}")
    public ResponseEntity<Object> updateBookingState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @PathVariable("bookingId") Long bookingId,
                                                     @RequestParam("approved") Boolean approved) {
        log.info("Updating booking approved status {} to {}, userId={}", bookingId, approved, userId);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }
}
