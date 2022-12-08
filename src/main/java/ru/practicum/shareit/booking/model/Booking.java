package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "booking")
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private LocalDateTime start;
    @Column(name = "end_date")
    private LocalDateTime end;
    @Transient
    private Item item;
    private int itemId;
    private Integer itemOwnerId;
    @Transient
    private User booker;
    private Integer userId;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
}
