package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity()
@Table(name = "item_request")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String description;
    @Transient
    private User requestor;
    private int userId;
    private LocalDateTime created;

    public ItemRequest(int id, String description, int userId, LocalDateTime created) {
        this.id = id;
        this.description = description;
        this.userId = userId;
        this.created = created;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * Objects.hash(id, description, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemRequest itemRequest = (ItemRequest) o;
        return id == itemRequest.id &&
                userId == itemRequest.userId &&
                Objects.equals(description, itemRequest.description);
    }
}
