package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    public List<ItemDto> getAllItems(int userId, int offset, int size) {
        var itemsList = itemRepository.findAllByOwnerIdOrderById(userId);
        var itemIdList = itemsList.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        var allItemsBookingList = bookingRepository.findAllByItemIdIn(
                        PageRequest.of(offset / size, size, Sort.by(Sort.Direction.ASC, "id")), itemIdList)
                .toList();
        var allItemsCommentList = commentRepository.findAllByItemIdIn(itemIdList);
        var allAuthorsIdList = allItemsCommentList.stream()
                .map(Comment::getAuthorId)
                .collect(Collectors.toList());
        Map<Integer, String> allUsers = userService.findAllUsersInList(allAuthorsIdList);
        var itemsDtoList = itemsList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemsDtoList.forEach(itemDto -> setBookingsToItemDto(itemDto, allItemsBookingList));
        itemsDtoList.forEach(itemDto -> addCommentsToItemDto(itemDto, allItemsCommentList, allUsers));
        return itemsDtoList;
    }

    private ItemDto addCommentsToItemDto(ItemDto itemDto, List<Comment> allItemsCommentList, Map<Integer, String> allUsers) {
        List<Comment> listComments = allItemsCommentList.stream()
                .filter(iDto -> Objects.equals(itemDto.getId(), iDto.getItemId()))
                .collect(Collectors.toList());
        listComments.forEach(comment ->
                comment.setAuthorName(allUsers.get(comment.getAuthorId())));
        itemDto.setComments(listComments);
        return itemDto;
    }

    private ItemDto setBookingsToItemDto(ItemDto item, List<Booking> allItemsBookingList) {
        var thisItemBookingListSorted = allItemsBookingList.stream()
                .filter(booking -> booking.getItemId() == item.getId())
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());

        var lastList = thisItemBookingListSorted.stream()
                .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                .collect(Collectors.toList());
        Booking last = lastList.size() > 0 ? lastList.get(lastList.size() - 1) : null;

        var nextList = thisItemBookingListSorted.stream()
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .collect(Collectors.toList());
        Booking next = nextList.size() > 0 ? nextList.get(0) : null;

        item.setLastBooking(BookingMapper.toBookingWithItemDto(last));
        item.setNextBooking(BookingMapper.toBookingWithItemDto(next));
        return item;
    }

    public ItemDto findItemDtoById(Integer id, Integer userId) {
        var itemOpt = itemRepository.findById(id);
        if (itemOpt.isPresent()) {
            var commentList = commentRepository.findAllByItemId(id);
            var userListFromComment = userService.findAllUsersInList(commentList.stream()
                    .map(Comment::getAuthorId)
                    .collect(Collectors.toList()));
            if (Objects.equals(itemOpt.get().getOwnerId(), userId)) {
                var allItemsBookingList = bookingRepository.findAllByItemIdIn(List.of(id));
                return addCommentsToItemDto(setBookingsToItemDto(ItemMapper.toItemDto(itemOpt.get()), allItemsBookingList),
                        commentList, userListFromComment);
            }
            return addCommentsToItemDto(ItemMapper.toItemDto(itemOpt.get()),
                    commentRepository.findAllByItemId(id), userListFromComment);
        } else {
            throw new NotFoundException("no item with this id!" + id);
        }
    }

    public ItemDto save(ItemDto itemDto, int userId) {
        if (userService.findUserById(userId).isEmpty()) {
            throw new NotFoundException("no owner with id " + userId); //check users exist
        }
        if (itemDto.getAvailable() == null || "".equals(itemDto.getName()) || "".equals(itemDto.getDescription())
                || itemDto.getName() == null || itemDto.getDescription() == null) {
            throw new ValidationException("create new unavailable item/empty name or description!");
        }

        itemDto.setOwner(userService.findUserById(userId).get());
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto updateFields(int id, ItemDto itemDto, int userId) {
        var itemOpt = itemRepository.findById(id);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("no item with id " + id);
        }
        if (itemOpt.get().getOwnerId() != userId) {
            throw new NotFoundException("this user isn't owner!");
        }
        return ItemMapper.toItemDto(itemRepository.save(patchItem(itemOpt.get(), itemDto)));
    }

    private Item patchItem(Item item, ItemDto itemDto) {
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getItemRequest() != null) {
            item.setItemRequest(itemDto.getItemRequest());
        }
        return item;
    }

    public List<ItemDto> findItemDtoByDescOrName(String text) {
        if (text.length() == 0) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableItemByNameOrDescr(text.toLowerCase(), true).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public Item findItemById(Integer id) {
        var itemOpt = itemRepository.findById(id);
        if (itemOpt.isPresent()) {
            Item item = itemOpt.get();
            var userOpt = userService.findUserById(item.getOwnerId());
            if (userOpt.isEmpty()) {
                throw new NotFoundException("no user with this id!" + item.getOwnerId());
            }
            item.setOwner(userOpt.get());
            return item;
        } else {
            throw new NotFoundException("no item with this id!" + id);
        }
    }

    public Comment addComment(int userId, Integer itemId, Comment comment) {
        if (comment.getText().isBlank()) {
            throw new ValidationException("empty comment in " + comment);
        }
        checkUserTakeItem(userId, itemId);
        comment.setAuthorId(userId);
        comment.setItemId(itemId);
        comment.setCreated(LocalDateTime.now());
        comment.setAuthorName(userService.findUserDtoById(userId).getName());
        commentRepository.save(comment);
        return comment;
    }

    private void checkUserTakeItem(int userId, int itemId) {
        if (bookingRepository
                .findByUserIdAndItemIdAndStartBefore(userId, itemId, LocalDateTime.now())
                .isEmpty()) {
            throw new ValidationException("cant find booking for item " + itemId + " from user " + userId);
        }
    }
}
