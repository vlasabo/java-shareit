package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemToRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemRequestTests {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    ItemRequestService itemRequestService;
    ItemRequestService itemRequestService2;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @InjectMocks
    private ItemRequestController controller;
    private ItemRequestDto itemRequestDto;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserService userService;
    @Mock
    ItemRepository itemRepository;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
        itemRequestDto = new ItemRequestDto(
                1,
                "desc",
                1,
                LocalDateTime.now());
        itemRequestService2 = new ItemRequestService(itemRequestRepository, userService, itemRepository);
    }

    @Test
    void saveNewRequest() throws Exception {
        when(itemRequestService.save(any(), anyInt()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void saveCorrectRequest() {
        when(userService.findUserDtoById(anyInt()))
                .thenReturn(new UserDto());
        when(itemRequestRepository.save(any()))
                .thenReturn(RequestMapper.toItemRequest(itemRequestDto));

        Assertions.assertEquals(itemRequestService2.save(itemRequestDto, 1), itemRequestDto);
    }

    @Test
    void getAllRequestsByUserId() throws Exception {
        when(itemRequestService.getAllRequestsByUserId(anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getAllRequestsByUserIdServiceTest() {
        when(userService.findUserDtoById(anyInt()))
                .thenReturn(new UserDto());
        when(itemRequestRepository.findAllByUserIdOrderByCreated(anyInt()))
                .thenReturn(List.of(RequestMapper.toItemRequest(itemRequestDto)));

        Assertions.assertEquals(itemRequestService2.getAllRequestsByUserId(1), List.of(itemRequestDto));
    }

    @Test
    void getRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyInt(), anyInt()))
                .thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));
    }

    @Test
    void getRequestByIdCorrect() {
        when(userService.findUserDtoById(anyInt()))
                .thenReturn(new UserDto());
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.of(RequestMapper.toItemRequest(itemRequestDto)));

        Assertions.assertEquals(itemRequestService2.getRequestById(1, 1), itemRequestDto);
    }

    @Test
    void getRequestByIdIncorrect() {
        when(userService.findUserDtoById(anyInt()))
                .thenReturn(new UserDto());
        when(itemRequestRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> itemRequestService2.getRequestById(1, 1));
    }

    @Test
    void getAll() throws Exception {
        when(itemRequestService.getAll(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));
    }

    @Test
    void getAllServiceTest() {
        when(itemRequestRepository.findAllByUserIdNot(any(), anyInt()))
                .thenReturn(new PageImpl<>(List.of(RequestMapper.toItemRequest(itemRequestDto))));

        Assertions.assertEquals(itemRequestService2.getAll(0, 20, 1), List.of(itemRequestDto));
    }

    @Test
    void mapperTest() {
        Item item = new Item();
        item.setName("name");
        item.setId(1);
        item.setDescription("desc");
        ItemToRequestDto item2 = new ItemToRequestDto();
        item2.setName("name");
        item2.setId(1);
        item2.setDescription("desc");
        Assertions.assertEquals(ItemMapper.toItemToRequestDto(item), item2);
    }
}
