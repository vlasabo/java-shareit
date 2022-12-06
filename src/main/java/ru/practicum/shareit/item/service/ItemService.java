package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;


    public List<ItemDto> getAllItems(int userId) {
        var itemsList = itemRepository.findAllByOwnerIdOrderById(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemsList.forEach(this::setBookingsToItemDto);
        return itemsList;
    }

    private ItemDto setBookingsToItemDto(ItemDto item) {
        item.setLastBooking(
                BookingMapper.toBookingWithItemDto(bookingRepository
                        .findFirstByItemIdAndStartBeforeOrderByStartDesc(item.getId(), LocalDateTime.now())));
        item.setNextBooking(
                BookingMapper.toBookingWithItemDto(bookingRepository
                        .findFirstByItemIdAndStartAfterOrderByStart(item.getId(), LocalDateTime.now())));
        return item;
    }

    public ItemDto findItemDtoById(Integer id, Integer userId) {
        var itemDtoOpt = itemRepository.findById(id);
        if (itemDtoOpt.isPresent()) {
            if (itemDtoOpt.get().getOwnerId().equals(userId)) {
                return setBookingsToItemDto(ItemMapper.toItemDto(itemDtoOpt.get()));
            }
            return ItemMapper.toItemDto(itemDtoOpt.get());
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
        return itemRepository.searchAvailableItemByNameOrDescr(text, true).stream()
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
}
