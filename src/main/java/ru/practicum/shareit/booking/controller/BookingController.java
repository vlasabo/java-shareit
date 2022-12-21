package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking addBooking(@Valid @RequestBody Booking booking,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.debug("add booking {}", booking);
        return bookingService.addBooking(booking, userId);
    }

    @PatchMapping("/{bookingId}")
    public Booking confirmationOrRejectionBookingRequest(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                         @PathVariable Integer bookingId,
                                                         @RequestParam Boolean approved) {
        log.debug("patch booking № {}", bookingId);
        return bookingService.confirmOrRejectBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @PathVariable Integer bookingId) {
        log.debug("get booking № {}", bookingId);
        return bookingService.getBooking(bookingId, userId);
    }


    @GetMapping()
    public List<Booking> getBookingForUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        log.debug("get booking for user {}", userId);
        return bookingService.getBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingForOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                            @RequestParam(defaultValue = "ALL") String state) {
        log.debug("get booking for owner {}", userId);
        return bookingService.getBookingsForOwner(userId, state);
    }
}
