package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.patcher.ObjectPatcher;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public List<ItemDto> getAllItems() {
        return itemRepository.getAllItems().stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto findItemDtoById(Integer id) {
        var itemDtoOpt = itemRepository.getItemById(id);
        if (itemDtoOpt.isPresent()) {
            return ItemMapper.toItemDto(itemDtoOpt.get());
        } else {
            throw new NotFoundException("no item with this id!");
        }
    }

    public Item save(Item item, int userId) {
        if (userService.findUserById(userId).isEmpty()) {
            throw new NotFoundException("no owner with id " + userId);//check users exist
        }
        item.setOwner(userService.findUserById(userId).get());
        return itemRepository.save(item);
    }

    public ItemDto updateFields(int id, ItemDto itemDto, int userId) throws NoSuchFieldException, IllegalAccessException {
        var itemOpt = itemRepository.getItemById(id);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("no item with id " + id);
        }
        if (itemOpt.get().getOwner().getId() != userId) {
            throw new IllegalAccessException("this user isn't owner!");
        }
        return ItemMapper.toItemDto((Item) ObjectPatcher.changeFields(itemDto, itemOpt.get()));
    }
}
