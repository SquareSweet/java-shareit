package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;


import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    BookingDto create(@Valid @RequestBody BookingDtoCreate bookingDto,
                      @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        return BookingMapper.toBookingDto(bookingService.create(booking, userId));
    }

    @GetMapping("/{bookingId}")
    BookingDto find(@PathVariable Long bookingId,
                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDto(bookingService.findById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDto> findByBooker(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String status,
            @RequestParam Optional<Integer> from,
            @RequestParam Optional<Integer> size) {

        return bookingService.findByBooker(userId, status, from.orElse(0), size.orElse(20)).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> findByOwner(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String status,
            @RequestParam Optional<Integer> from,
            @RequestParam Optional<Integer> size) {

        return bookingService.findByOwner(userId, status, from.orElse(0), size.orElse(20)).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto updateState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable("bookingId") Long bookingId,
                                  @RequestParam("approved") Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.updateStatus(bookingId, userId, approved));
    }
}
