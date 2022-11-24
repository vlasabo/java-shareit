package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.patcher.ItemPatcher;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    public List<ItemDto> getAllItems(int userId) {
        return itemRepository.getAllItems().stream()
                .filter(x -> x.getOwner().getId() == userId)
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto findItemDtoById(Integer id) {
        var itemDtoOpt = itemRepository.getItemById(id);
        if (itemDtoOpt.isPresent()) {
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
        return ItemMapper.toItemDto(itemRepository.save(itemDto));
    }

    public ItemDto updateFields(int id, ItemDto itemDto, int userId) {
        var itemOpt = itemRepository.getItemById(id);
        if (itemOpt.isEmpty()) {
            throw new NotFoundException("no item with id " + id);
        }
        if (itemOpt.get().getOwner().getId() != userId) {
            throw new NotFoundException("this user isn't owner!");
        }
        return ItemMapper.toItemDto(ItemPatcher.patchItem(itemOpt.get(), itemDto));
    }

    public List<ItemDto> findItemDtoByDescOrName(String text) {
        if (text.length() == 0) {
            return new ArrayList<>();
        }
        return itemRepository.findItemDtoByDescOrName(text);
    }
}
