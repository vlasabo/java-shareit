package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.practicum.shareit.exception.CustomValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;


    public UserDto save(User user) {
        User userSaved = userRepository.save(user);
        return UserMapper.toUserDto(userSaved);
    }

    private boolean checkEmailUniqueness(User user) {
        String email = user.getEmail();
        return userRepository.findAll()
                .stream()
                .anyMatch(checkedUser -> (Objects.equals(checkedUser.getEmail(), email)
                        && !Objects.equals(checkedUser.getId(), user.getId())));
    }

    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public UserDto findUserDtoById(int id) {
        var userDtoOpt = userRepository.findById(id);
        if (userDtoOpt.isPresent()) {
            return UserMapper.toUserDto(userDtoOpt.get());
        } else {
            throw new NotFoundException("no user with id" + id);
        }
    }

    public Optional<User> findUserById(int id) {
        return userRepository.findById(id);
    }

    public void deleteUserById(int id) {
        if (findUserById(id).isEmpty()) {
            throw new NotFoundException("no user with id " + id);
        } //check that user exists
        userRepository.deleteById(id);
    }

    public UserDto update(User user) {
        if (checkEmailUniqueness(user)) {
            throw new CustomValidateException("email isn't unique");
        }
        if (findUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("no user with id " + user.getId());
        } //check that user exists

        return UserMapper.toUserDto(userRepository.save(user));
    }

    public UserDto updateFields(int id, UserDto user) {
        if (checkEmailUniqueness(user)) {
            throw new CustomValidateException("email isn't unique");
        }
        var userFromDbOpt = findUserById(id);
        if (userFromDbOpt.isEmpty()) {
            throw new NotFoundException("mo user with id " + id);
        }
        return save(patchUser(userFromDbOpt.get(), user));
    }

    private User patchUser(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return user;
    }

    public Map<Integer, String> findAllUsersInList(List<Integer> usersId) {
        return userRepository.findAllByIdIn(usersId).stream()
                .collect(Collectors.toConcurrentMap(User::getId, User::getName));
    }

}
