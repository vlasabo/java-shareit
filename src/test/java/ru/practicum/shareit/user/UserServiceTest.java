package ru.practicum.shareit.user;


import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.webjars.NotFoundException;
import ru.practicum.shareit.exception.CustomValidateException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {


    @Test
    public void createNewUser() {
        var usersList = createUserDtoSeveralTimes(1);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(0));

        UserService userService = new UserService(userRepository);
        Assertions.assertEquals(userService.save(usersList.get(0)), usersList.get(0));
    }

    @Test
    public void getAllUsers() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);

        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(0));
        userService.save(usersList.get(0));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        userService.save(usersList.get(1));
        Assertions.assertEquals(userService.getAllUsers(),
                usersList.stream()
                        .map(UserMapper::toUserDto)
                        .collect(Collectors.toList()));
    }

    @Test
    public void findUserByIdWithCorrectId() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));

        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(0));
        userService.save(usersList.get(0));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        userService.save(usersList.get(1));
        Assertions.assertEquals(userService.findUserById(1), Optional.of(usersList.get(1)));
    }

    @Test
    public void findUserByIdWithIncorrectId() {

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findById(99))
                .thenReturn(Optional.empty());

        UserService userService = new UserService(userRepository);
        Assertions.assertEquals(userService.findUserById(99), Optional.empty());
    }

    @Test
    public void findUserDtoByIdWithCorrectId() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));

        UserService userService = new UserService(userRepository);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(0));
        userService.save(usersList.get(0));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        userService.save(usersList.get(1));
        Assertions.assertEquals(userService.findUserDtoById(1), UserMapper.toUserDto(usersList.get(1)));
    }

    @Test
    public void findUserDtoByIdWithIncorrectId() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findById(99))
                .thenReturn(Optional.empty());

        UserService userService = new UserService(userRepository);
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserDtoById(99));
    }

    @Test
    public void deleteUserWithIncorrectId() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        UserService userService = new UserService(userRepository);

        Assertions.assertThrows(NotFoundException.class, () -> userService.deleteUserById(99));
    }

    @Test
    public void deleteUserWithCorrectId() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        UserService userService = new UserService(userRepository);

        Assertions.assertDoesNotThrow(() -> userService.deleteUserById(1));
    }

    @Test
    public void updateUserWithNotUniqueEmail() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        UserService userService = new UserService(userRepository);
        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com1");
        Assertions.assertThrows(CustomValidateException.class, () -> userService.update(usersList.get(1)));
    }

    @Test
    public void updateUserWithIncorrectId() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository);
        User upd = usersList.get(1);
        upd.setId(1);
        Assertions.assertThrows(NotFoundException.class, () -> userService.update(usersList.get(1)));
    }

    @Test
    public void updateCorrectUser() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        UserService userService = new UserService(userRepository);
        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com3");
        Assertions.assertEquals(userService.update(usersList.get(1)), UserMapper.toUserDto(upd));
    }

    @Test
    public void updateFieldsUserWithNotUniqueEmail() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        UserService userService = new UserService(userRepository);
        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com1");
        Assertions.assertThrows(CustomValidateException.class, () ->
                userService.updateFields(1, UserMapper.toUserDto(usersList.get(1))));
    }

    @Test
    public void updateFieldsUserWithIncorrectId() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.empty());
        UserService userService = new UserService(userRepository);
        User upd = usersList.get(1);
        upd.setId(1);
        Assertions.assertThrows(NotFoundException.class, () ->
                userService.updateFields(1, UserMapper.toUserDto(usersList.get(1))));
    }

    @Test
    public void updateFieldsCorrectUser() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        UserService userService = new UserService(userRepository);
        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com3");
        Assertions.assertEquals(userService.updateFields(1, UserMapper.toUserDto(upd)), UserMapper.toUserDto(upd));
    }

    @Test
    public void findAllUsersInList() {
        var usersList = createUserSeveralTimes(2);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.findAllByIdIn(List.of(1)))
                .thenReturn(List.of(usersList.get(0)));
        UserService userService = new UserService(userRepository);
        Assertions.assertEquals(userService.findAllUsersInList(List.of(1)),
                Map.of(usersList.get(0).getId(), usersList.get(0).getName()));
    }

    private List<UserDto> createUserDtoSeveralTimes(int n) {
        List<UserDto> usersList = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            UserDto userDto = new UserDto();
            userDto.setName("test" + i);
            userDto.setEmail("testEmail@email.com" + i);
            usersList.add(userDto);
        }
        return usersList;
    }

    private List<User> createUserSeveralTimes(int n) {
        List<User> usersList = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            User user = new User();
            user.setName("test" + i);
            user.setEmail("testEmail@email.com" + i);
            usersList.add(user);
        }
        return usersList;
    }
}
