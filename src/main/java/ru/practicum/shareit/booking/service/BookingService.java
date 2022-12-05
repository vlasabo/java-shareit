package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;


@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking addBooking(Booking booking, Integer userId) {
        booking.setUserId(userId);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(itemService.findItemDtoById(booking.getItemId()));
        checkItemAvailable(booking);
        booking.setBooker(getUser(booking));
        checkBooking(booking);
        return bookingRepository.save(booking);
    }

    private void checkBooking(Booking booking) {
        if (booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("incorrect adding booking" + booking.getId());
        }
    }

    private User getUser(Booking booking) {
        Optional<User> userOpt = userService.findUserById(booking.getUserId());
        if (userOpt.isEmpty()) {
            throw new NotFoundException("incorrect adding booking" + booking.getId());
        } else {
            return UserMapper.toUserDto(userOpt.get());
        }
    }

    private void checkItemAvailable(Booking booking) {
        if (!itemService.findItemDtoById(booking.getItemId()).getAvailable()) {
            throw new ValidationException("incorrect adding booking" + booking.getId());
        }
    }

    public Booking confirmOrRejectBooking(Integer bookingId, Boolean approved, int userId) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("incorrect bookingId" + bookingId);
        }
        if (bookingOpt.get().getUserId() != userId) {
            throw new ValidationException(userId + " isn't owner booking " + bookingId);
        }

        Booking booking = bookingOpt.get();
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return booking;
    }
}
