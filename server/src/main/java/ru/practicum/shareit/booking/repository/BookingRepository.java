package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findAllByUserIdOrderByStartDesc(Pageable pageable, Integer id);

    List<Booking> findAllByUserIdAndEndBeforeOrderByStartDesc(Integer id, LocalDateTime end);

    List<Booking> findAllByUserIdAndStartAfterOrderByStartDesc(Integer id, LocalDateTime start);

    List<Booking> findAllByUserIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByUserIdAndStatusOrderByStartDesc(Integer id, BookingStatus status);

    Page<Booking> findAllByItemOwnerIdOrderByStartDesc(Pageable pageable, Integer id);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(Integer id, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(Integer id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Integer id, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStatusOrderByStartDesc(Integer id, BookingStatus status);

    Optional<Booking> findByUserIdAndItemIdAndStartBefore(Integer userId, Integer itemId, LocalDateTime start);

    Page<Booking> findAllByItemIdIn(Pageable pageable, List<Integer> itemsId);

    List<Booking> findAllByItemIdIn(List<Integer> itemsId);
}
