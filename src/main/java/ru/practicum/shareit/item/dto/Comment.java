package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Setter
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String text;
    Integer itemId;
    Integer authorId;
    LocalDateTime created;
    @Transient
    String authorName;
}
