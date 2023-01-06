package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class ItemTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    ItemService itemService;

    @InjectMocks
    private ItemController controller;

    @Mock
    UserService userService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    ItemRepository itemRepository;
    @Mock
    CommentRepository commentRepository;
    ItemService itemService2;

    private ItemDto itemDto;
    private UserDto userDto;
    private List<Item> itemsList;

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
        itemsList = createItemSeveralTimes(2);
        itemService2 = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
    }

    @Test
    public void createNewItem() {
        when(itemRepository.save(any()))
                .thenReturn(itemsList.get(0));

        when(userService.findUserById(1))
                .thenReturn(Optional.of(new User()));

        assertEquals(itemService2.save(ItemMapper.toItemDto(itemsList.get(0)), 1),
                ItemMapper.toItemDto(itemsList.get(0)));
    }

    @Test
    public void createNewItemWithIncorrectUserId() {
        when(userService.findUserById(1))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService2.save(ItemMapper.toItemDto(itemsList.get(0)), 1));
    }

    @Test
    public void createNewItemWithIncorrectItemDescription() {

        when(userService.findUserById(1))
                .thenReturn(Optional.of(new User()));
        Item item = itemsList.get(0);
        item.setDescription("");

        assertThrows(ValidationException.class, () -> itemService2.save(ItemMapper.toItemDto(itemsList.get(0)), 1));
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
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(content().json(itemDtoAsJsonString));
    }

    @Test
    public void getAllItems() {
        when(itemRepository.findAllByOwnerIdOrderById(1))
                .thenReturn(itemsList);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        assertEquals(itemService2.getAllItems(1, 0, 20),
                itemsList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
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
    public void findItemDtoById() {
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(1))
                .thenReturn(new ArrayList<>());

        assertEquals(itemService2.findItemDtoById(1, 1),
                ItemMapper.toItemDto(itemsList.get(0)));
    }

    @Test
    public void findItemByIdCorrect() {
        var item = itemsList.get(0);
        item.setOwnerId(1);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));

        assertEquals(itemService2.findItemById(1), item);
    }

    @Test
    public void findItemByIdIncorrectUserId() {
        var item = itemsList.get(0);
        item.setOwnerId(99);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemService2.findItemById(1));
    }

    @Test
    public void findItemByIdIncorrectItemId() {
        assertThrows(NotFoundException.class, () -> itemService2.findItemById(99));
    }

    @Test
    public void findItemDtoByWrongId() {
        assertThrows(NotFoundException.class, () -> itemService2.findItemDtoById(99, 1));
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
    public void findItemDtoByDescOrNameCorrect() {
        when(itemRepository.searchAvailableItemByNameOrDescr(anyString(), anyBoolean()))
                .thenReturn(itemsList);

        assertEquals(itemService2.findItemDtoByDescOrName("test"), itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    @Test
    public void findItemDtoByDescOrNameWithoutTextToSearch() {
        assertEquals(itemService2.findItemDtoByDescOrName("").size(), 0);
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
    public void correctUpdateFields() {
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRequest request = new ItemRequest(1, " d", 1, LocalDateTime.now());
        item.setItemRequest(request);

        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(item))
                .thenReturn(item);

        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setItemRequest(request);
        assertEquals(itemService2.updateFields(1, itemDto, 1), ItemMapper.toItemDto(item));
    }

    @Test
    public void incorrectUpdateFieldsNoItemWithId() {
        Item item = itemsList.get(0);
        when(itemRepository.findById(99))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService2.updateFields(99, ItemMapper.toItemDto(item), 1));
    }

    @Test
    public void incorrectUpdateFieldsWrongUser() {
        Item item = itemsList.get(0);

        assertThrows(NotFoundException.class, () -> itemService2.updateFields(1, ItemMapper.toItemDto(item), 99));
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

    @Test
    public void addCommentCorrect() {
        var item = itemsList.get(0);
        item.setOwnerId(1);
        Comment comment = new Comment();
        comment.setItemId(1);
        comment.setText("aaa");
        comment.setId(1);
        comment.setAuthorId(1);
        User user = new User();
        user.setId(1);

        when(userService.findUserDtoById(1))
                .thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findByUserIdAndItemIdAndStartBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new Booking()));

        assertEquals(itemService2.addComment(1, 1, comment), comment);
    }

    @Test
    public void addCommentIncorrectEmptyComment() {
        var item = itemsList.get(0);
        item.setOwnerId(1);
        Comment comment = new Comment();
        comment.setItemId(1);
        comment.setText("");
        comment.setId(1);
        comment.setAuthorId(1);

        assertThrows(ValidationException.class, () -> itemService2.addComment(1, 1, comment));
    }

    @Test
    public void addCommentIncorrectNoBooking() {
        var item = itemsList.get(0);
        item.setOwnerId(1);
        Comment comment = new Comment();
        comment.setItemId(1);
        comment.setText("aaa");
        comment.setId(1);
        comment.setAuthorId(1);

        when(bookingRepository.findByUserIdAndItemIdAndStartBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> itemService2.addComment(1, 1, comment));
    }

    private List<Item> createItemSeveralTimes(int n) {
        List<Item> itemsList = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            Item item = new Item();
            item.setName("test" + i);
            item.setDescription("testDescription" + i);
            item.setAvailable(true);
            itemsList.add(item);
        }
        return itemsList;
    }
}
