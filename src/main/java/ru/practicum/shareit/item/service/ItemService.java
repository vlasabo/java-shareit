package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
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
        if (item.getOwner() == null) {
            throw new NotFoundException("no owner!");
        }
        return itemRepository.save(item);
    }
}
