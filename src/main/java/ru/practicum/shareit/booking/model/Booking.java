package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * Objects.hash(id, userId, itemId, itemOwnerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id) &&
                Objects.equals(userId, booking.userId) &&
                Objects.equals(itemId, booking.itemId) &&
                Objects.equals(itemOwnerId, booking.itemOwnerId);

    }
}
