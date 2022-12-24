package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestDto save(ItemRequestDto itemRequestDto, int userId) {
        userService.findUserDtoById(userId);
        itemRequestDto.setUserId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = RequestMapper.toItemRequest(itemRequestDto);
        return RequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public List<ItemRequestDto> getAllRequestsByUserId(int userId) {
        userService.findUserDtoById(userId);
        var resultList = itemRequestRepository.findAllByUserIdOrderByCreated(userId).stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        resultList.forEach(this::fillItemsList);
        return resultList;
    }

    private void fillItemsList(ItemRequestDto itemRequestDto) {
        Integer itemRequestId = itemRequestDto.getId();
        var itemsList = itemRepository.findAllByRequestId(itemRequestId).stream()
                .map(ItemMapper::toItemToRequestDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemsList);
    }

    public ItemRequestDto getRequestById(Integer id, Integer userId) {
        userService.findUserDtoById(userId);
        var itemRequestOptional = itemRequestRepository.findById(id);
        if (itemRequestOptional.isEmpty()) {
            throw new NotFoundException("no request with id" + id);
        } else {
            ItemRequestDto result = RequestMapper.toItemRequestDto(itemRequestOptional.get());
            fillItemsList(result);
            return result;
        }
    }

    public List<ItemRequestDto> getAll(Integer offset, Integer size, Integer userId) {
        var resultList = itemRequestRepository.findAllByUserIdNot(
                        PageRequest.of(offset, size, Sort.by(Sort.Direction.ASC, "created")), userId)
                .stream()
                .map(RequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        resultList.forEach(this::fillItemsList);
        return resultList;
    }
}
