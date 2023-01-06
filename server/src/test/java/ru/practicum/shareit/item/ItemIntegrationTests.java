package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.List;

@SpringBootTest
public class ItemIntegrationTests {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    User user;
    User user2;
    ItemDto item;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1);
        user.setEmail("mail@mail.com");
        user.setName("name");
        user2 = new User();
        user2.setId(2);
        user2.setEmail("mail2@mail.com");
        user2.setName("name2");
        userService.save(user);
        userService.save(user2);

        item = new ItemDto();
        item.setId(1);
        item.setOwnerId(2);
        item.setOwner(user2);
        item.setName("itemName");
        item.setDescription("itemDesc");
        item.setAvailable(true);
    }

    @Test
    void save() {
        Assertions.assertEquals(itemService.save(item, 2), item);
    }

    @Test
    void getAllItems() {
        itemService.save(item, 2);
        Assertions.assertEquals(itemService.getAllItems(2, 0, 20), List.of(item));
    }

    @Test
    void findItemDtoById() {
        itemService.save(item, 2);
        Assertions.assertEquals(itemService.findItemById(1), ItemMapper.toItem(item));
    }

    @Test
    void addComment() {
        Comment comment = new Comment();
        comment.setText("comment");
        itemService.save(item, 2);
        Assertions.assertThrows(ValidationException.class,
                () -> itemService.addComment(1, 1, comment));
    }
}
