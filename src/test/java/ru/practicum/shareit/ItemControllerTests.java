package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    ItemService itemService;

    @InjectMocks
    private ItemController controller;

    private ItemDto itemDto;
    private UserDto userDto;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(
                1,
                "vlasabo",
                "Aa@bb.com");

        itemDto = new ItemDto("name", "desc", true, 1, null);
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDto)));
    }

    @Test
    void saveNewItemTest() throws Exception {
        when(itemService.save(any(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemService.getAllItems(1, 0, 20))
                .thenReturn(List.of(itemDto));

        String itemDtoAsJsonString = "[{\"id\":1,\"name\":\"name\",\"description\":\"desc\",\"available\":true,\"owner\":null," +
                "\"itemRequest\":null,\"ownerId\":null,\"requestId\":null,\"lastBooking\":null," +
                "\"nextBooking\":null,\"comments\":[]}]";
        this.mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(itemDtoAsJsonString));
    }

    @Test
    void findItemByIdTest() throws Exception {
        when(itemService.findItemDtoById(1, 1))
                .thenReturn(itemDto);

        this.mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    void findItemBySearchingTest() throws Exception {
        when(itemService.findItemDtoByDescOrName("desc"))
                .thenReturn(List.of(itemDto));

        this.mockMvc.perform(get("/items/search?text=desc")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemDto))));
    }

    @Test
    void patchItemTest() throws Exception {
        mockMvc.perform(patch("/items")
                .content(objectMapper.writeValueAsString(itemDto)));
        userDto.setName("updated2");
        when(itemService.updateFields(anyInt(), any(), anyInt()))
                .thenReturn(itemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk());
    }

    @Test
    void postCommentTest() throws Exception {
        mockMvc.perform(patch("/items")
                .content(objectMapper.writeValueAsString(itemDto)));

        Comment comment = new Comment();
        comment.setAuthorName("vlasabo");
        comment.setItemId(1);
        comment.setAuthorId(1);
        comment.setText("comment");
        comment.setId(1);

        mockMvc.perform(post("/items/1/comment")
                        .content(objectMapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
