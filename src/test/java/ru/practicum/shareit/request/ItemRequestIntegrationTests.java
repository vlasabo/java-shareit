package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class ItemRequestIntegrationTests {
    @Autowired
    ItemRequestService itemRequestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    User user;
    User user2;
    Item item;
    ItemRequestDto itemRequestDto;

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

        item = new Item();
        item.setId(1);
        item.setOwnerId(2);
        item.setOwner(user2);
        item.setName("itemName");
        item.setDescription("itemDesc");
        item.setAvailable(true);
        itemService.save(ItemMapper.toItemDto(item), 2);

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setItems(List.of(ItemMapper.toItemToRequestDto(item)));
        itemRequestDto.setCreated(LocalDateTime.now());
        itemRequestDto.setUserId(1);
        itemRequestDto.setId(1);
        itemRequestDto.setDescription("desc");
    }

    @Test
    void addRequest() {
        Assertions.assertEquals(itemRequestService.save(itemRequestDto, 1), itemRequestDto);
    }

    @Test
    void getAllRequestsByUserId() {
        itemRequestService.save(itemRequestDto, 1);
        Assertions.assertEquals(itemRequestService.getAllRequestsByUserId(1), List.of(itemRequestDto));
    }

    @Test
    void getRequestById() {
        itemRequestService.save(itemRequestDto, 1);
        Assertions.assertEquals(itemRequestService.getRequestById(1, 1), itemRequestDto);
    }

    @Test
    void getAll() {
        itemRequestService.save(itemRequestDto, 1);
        Assertions.assertEquals(itemRequestService.getAll(0, 20, 2), List.of(itemRequestDto));
    }
}
