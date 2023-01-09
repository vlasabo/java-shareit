package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    public Booking addBooking(Booking booking, Integer userId) {
        checkBooking(booking);
        booking.setUserId(userId);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(buildBooking(booking, userId));
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

    private void checkItemAvailable(Booking booking, Integer userId) {
        if (!itemService.findItemDtoById(booking.getItemId(), userId).getAvailable()) {
            throw new ValidationException("incorrect adding booking" + booking.getId());
        }
    }

    public Booking confirmOrRejectBooking(Integer bookingId, Boolean approved, Integer userId) {
        var bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("incorrect bookingId" + bookingId);
        }
        if (bookingOpt.get().getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("try to change status approved booking" + bookingId);
        }
        Booking booking = buildBooking(bookingOpt.get(), userId);
        if (!Objects.equals(booking.getItem().getOwnerId(), userId)) {
            throw new NotFoundException(userId + " isn't owner item " + booking.getItem());
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return booking;
    }

    public Booking getBooking(Integer bookingId, Integer userId) {
        var bookingOpt = bookingRepository.findById(bookingId);
        Booking booking;

        if (bookingOpt.isEmpty()) {
            throw new NotFoundException("incorrect bookingId " + bookingId);
        } else {
            booking = buildBooking(bookingOpt.get(), userId);
        }

        if (!Objects.equals(booking.getUserId(), userId) && booking.getItem().getOwner().getId() != userId) {
            throw new NotFoundException("no rights to get booking with id " + bookingId);
        }

        return booking;
    }

    private Booking buildBooking(Booking booking, Integer userId) {
        Item item = itemService.findItemById(booking.getItemId());
        booking.setItem(item);
        checkItemAvailable(booking, userId);
        booking.setBooker(getUser(booking));
        booking.setItemOwnerId(item.getOwnerId());
        if (Objects.equals(booking.getItem().getOwnerId(), booking.getUserId())) {
            throw new NotFoundException("requesting item from itself in booking" + booking.getId());
        }
        return booking;
    }

    public List<Booking> getBookingsForUser(Integer userId, String stateString, Integer offset, Integer size) {
        State state = State.valueOf(stateString);
        userService.findUserDtoById(userId);
        List<Booking> resultList;

        switch (state) {
            case PAST:
                resultList = bookingRepository.findAllByUserIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                resultList = bookingRepository.findAllByUserIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case CURRENT:
                resultList = bookingRepository.findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case REJECTED:
                resultList = bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                resultList = bookingRepository.findAllByUserIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            default:
                resultList = bookingRepository.findAllByUserIdOrderByStartDesc(
                        PageRequest.of(offset / size, size, Sort.by(Sort.Direction.ASC, "id")), userId).toList();
        }
        return resultList.stream()
                .map(booking -> buildBooking(booking, userId))
                .collect(Collectors.toList());
    }


    public List<Booking> getBookingsForOwner(Integer userId, String stateString, Integer offset, Integer size) {
        State state = State.valueOf(stateString);
        userService.findUserDtoById(userId);
        List<Booking> resultList;

        switch (state) {
            case PAST:
                resultList = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                resultList = bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case CURRENT:
                resultList = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case REJECTED:
                resultList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case WAITING:
                resultList = bookingRepository.findAllByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            default:
                resultList = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(
                        PageRequest.of(offset / size, size, Sort.by(Sort.Direction.ASC, "id")), userId).toList();
        }
        return resultList.stream()
                .map(booking -> buildBooking(booking, userId))
                .collect(Collectors.toList());
    }

}
