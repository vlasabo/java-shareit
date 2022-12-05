package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.service.BookingService;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addBooking(@RequestBody Booking booking,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.debug("add booking {}", booking);
        return bookingService.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking confirmationOrRejectionBookingRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                                         @PathVariable Integer bookingId,
                                                         @RequestParam Boolean approved) {
        log.debug("patch booking № {}", bookingId);
        return bookingService.confirmOrRejectBooking(bookingId, approved, userId);
    }
}
