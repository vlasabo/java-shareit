package ru.practicum.shareit.booking;

import org.springframework.data.annotation.Transient;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

public class Booking {
    private int id;
    private LocalDateTime start;
    private LocalDateTime end;
    @Transient
    private Item item;
    private int itemId;
    @Transient
    private User booker;
    private int userId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
