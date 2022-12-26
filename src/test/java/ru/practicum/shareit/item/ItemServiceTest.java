package ru.practicum.shareit.item;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webjars.NotFoundException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTest {

    @Test
    public void createNewItem() {
        var itemsList = createItemSeveralTimes(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.save(any()))
                .thenReturn(itemsList.get(0));

        UserService userService = Mockito.mock(UserService.class);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(new User()));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.save(ItemMapper.toItemDto(itemsList.get(0)), 1),
                ItemMapper.toItemDto(itemsList.get(0)));
    }

    @Test
    public void createNewItemWithIncorrectUserId() {
        var itemsList = createItemSeveralTimes(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.save(any()))
                .thenReturn(itemsList.get(0));

        UserService userService = Mockito.mock(UserService.class);
        when(userService.findUserById(1))
                .thenReturn(Optional.empty());
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(NotFoundException.class, () -> itemService.save(ItemMapper.toItemDto(itemsList.get(0)), 1));
    }

    @Test
    public void createNewItemWithIncorrectItemDescription() {
        var itemsList = createItemSeveralTimes(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.save(any()))
                .thenReturn(itemsList.get(0));

        UserService userService = Mockito.mock(UserService.class);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(new User()));
        Item item = itemsList.get(0);
        item.setDescription("");
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(ValidationException.class, () -> itemService.save(ItemMapper.toItemDto(itemsList.get(0)), 1));
    }

    @Test
    public void getAllItems() {
        var itemsList = createItemSeveralTimes(3);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findAllByOwnerIdOrderById(1))
                .thenReturn(itemsList);

        UserService userService = Mockito.mock(UserService.class);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(new User()));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.getAllItems(1, 0, 20),
                itemsList.stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
    }

    @Test
    public void findItemDtoById() {
        var itemsList = createItemSeveralTimes(1);
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        when(commentRepository.findAllByItemId(1))
                .thenReturn(new ArrayList<>());
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.findItemDtoById(1, 1),
                ItemMapper.toItemDto(itemsList.get(0)));
    }

    @Test
    public void findItemDtoByWrongId() {
        var itemsList = createItemSeveralTimes(1);
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        when(commentRepository.findAllByItemId(1))
                .thenReturn(new ArrayList<>());
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(NotFoundException.class, () -> itemService.findItemDtoById(99, 1));
    }

    @Test
    public void correctUpdateFields() {
        var itemsList = createItemSeveralTimes(1);
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRequest request = new ItemRequest(1, " d", 1, LocalDateTime.now());
        item.setItemRequest(request);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(item))
                .thenReturn(item);

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        when(commentRepository.findAllByItemId(1))
                .thenReturn(new ArrayList<>());
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setItemRequest(request);
        assertEquals(itemService.updateFields(1, itemDto, 1), ItemMapper.toItemDto(item));
    }

    @Test
    public void incorrectUpdateFieldsNoItemWithId() {
        var itemsList = createItemSeveralTimes(1);
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(99))
                .thenReturn(Optional.empty());
        when(itemRepository.save(item))
                .thenReturn(item);

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        when(commentRepository.findAllByItemId(1))
                .thenReturn(new ArrayList<>());
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(NotFoundException.class, () -> itemService.updateFields(99, ItemMapper.toItemDto(item), 1));
    }

    @Test
    public void incorrectUpdateFieldsWrongUser() {
        var itemsList = createItemSeveralTimes(1);
        Item item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(item))
                .thenReturn(item);

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(99))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        when(commentRepository.findAllByItemId(1))
                .thenReturn(new ArrayList<>());
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(NotFoundException.class, () -> itemService.updateFields(1, ItemMapper.toItemDto(item), 99));
    }

    @Test
    public void findItemDtoByDescOrNameCorrect() {
        var itemsList = createItemSeveralTimes(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.searchAvailableItemByNameOrDescr(anyString(), anyBoolean()))
                .thenReturn(itemsList);

        UserService userService = Mockito.mock(UserService.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.findItemDtoByDescOrName("test"), itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }

    @Test
    public void findItemDtoByDescOrNameWithoutTextToSearch() {
        var itemsList = createItemSeveralTimes(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.searchAvailableItemByNameOrDescr(anyString(), anyBoolean()))
                .thenReturn(itemsList);

        UserService userService = Mockito.mock(UserService.class);
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.findItemDtoByDescOrName("").size(), 0);
    }

    @Test
    public void findItemByIdCorrect() {
        var itemsList = createItemSeveralTimes(1);
        var item = itemsList.get(0);
        item.setOwnerId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.findItemById(1), item);
    }

    @Test
    public void findItemByIdIncorrectUserId() {
        var itemsList = createItemSeveralTimes(1);
        var item = itemsList.get(0);
        item.setOwnerId(99);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(NotFoundException.class, () -> itemService.findItemById(1));
    }

    @Test
    public void findItemByIdIncorrectItemId() {
        var itemsList = createItemSeveralTimes(1);
        var item = itemsList.get(0);
        item.setOwnerId(99);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(1))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(NotFoundException.class, () -> itemService.findItemById(99));
    }

    @Test
    public void addCommentCorrect() {
        var itemsList = createItemSeveralTimes(1);
        var item = itemsList.get(0);
        item.setOwnerId(1);
        Comment comment = new Comment();
        comment.setItemId(1);
        comment.setText("aaa");
        comment.setId(1);
        comment.setAuthorId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        when(userService.findUserDtoById(1))
                .thenReturn(UserMapper.toUserDto(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findByUserIdAndItemIdAndStartBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new Booking()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertEquals(itemService.addComment(1, 1, comment), comment);
    }

    @Test
    public void addCommentIncorrectEmptyComment() {
        var itemsList = createItemSeveralTimes(1);
        var item = itemsList.get(0);
        item.setOwnerId(1);
        Comment comment = new Comment();
        comment.setItemId(1);
        comment.setText("");
        comment.setId(1);
        comment.setAuthorId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        when(userService.findUserDtoById(1))
                .thenReturn(UserMapper.toUserDto(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findByUserIdAndItemIdAndStartBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.of(new Booking()));
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(ValidationException.class, () -> itemService.addComment(1, 1, comment));
    }

    @Test
    public void addCommentIncorrectNoBooking() {
        var itemsList = createItemSeveralTimes(1);
        var item = itemsList.get(0);
        item.setOwnerId(1);
        Comment comment = new Comment();
        comment.setItemId(1);
        comment.setText("aaa");
        comment.setId(1);
        comment.setAuthorId(1);
        ItemRepository itemRepository = Mockito.mock(ItemRepository.class);
        when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        UserService userService = Mockito.mock(UserService.class);
        User user = new User();
        user.setId(1);
        when(userService.findUserById(1))
                .thenReturn(Optional.of(user));
        when(userService.findUserDtoById(1))
                .thenReturn(UserMapper.toUserDto(user));
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        when(bookingRepository.findByUserIdAndItemIdAndStartBefore(anyInt(), anyInt(), any()))
                .thenReturn(Optional.empty());
        CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
        ItemService itemService = new ItemService(itemRepository, userService, bookingRepository, commentRepository);
        assertThrows(ValidationException.class, () -> itemService.addComment(1, 1, comment));
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
