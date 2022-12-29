package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.webjars.NotFoundException;
import ru.practicum.shareit.exception.CustomValidateException;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserTests {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    UserService userService;
    @InjectMocks
    UserService userService2;

    @InjectMocks
    private UserController controller;

    @Mock
    private UserRepository userRepository;

    private UserDto userDto;
    private List<UserDto> usersDtoList;
    private List<User> usersList;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        userDto = new UserDto(
                1,
                "vlasabo",
                "Aa@bb.com");
        usersDtoList = createUserDtoSeveralTimes(2);
        usersList = createUserSeveralTimes(2);
    }

    @Test
    public void createNewUser() {

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersDtoList.get(0));

        Assertions.assertEquals(userService2.save(usersDtoList.get(0)), usersDtoList.get(0));
    }

    @Test
    void saveNewUserTest() throws Exception {
        when(userService.save(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserTest() throws Exception {
        userDto.setName("updated");
        when(userService.update(any()))
                .thenReturn(userDto);

        mockMvc.perform(put("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateUserWithNotUniqueEmail() {
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);

        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com1");
        Assertions.assertThrows(CustomValidateException.class, () -> userService2.update(usersList.get(1)));
    }

    @Test
    public void updateUserWithIncorrectId() {
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.empty());

        User upd = usersList.get(1);
        upd.setId(1);
        Assertions.assertThrows(NotFoundException.class, () -> userService2.update(usersList.get(1)));
    }

    @Test
    public void updateCorrectUser() {

        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));

        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com3");
        Assertions.assertEquals(userService2.update(usersList.get(1)), UserMapper.toUserDto(upd));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllUsers() {
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersDtoList.get(0));
        userService2.save(usersDtoList.get(0));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersDtoList.get(1));
        userService2.save(usersDtoList.get(1));
        Assertions.assertEquals(userService2.getAllUsers(),
                usersDtoList.stream()
                        .map(UserMapper::toUserDto)
                        .collect(Collectors.toList()));
    }

    @Test
    public void findAllUsersInList() {
        Mockito.when(userRepository.findAllByIdIn(List.of(1)))
                .thenReturn(List.of(usersList.get(0)));

        Assertions.assertEquals(userService2.findAllUsersInList(List.of(1)),
                Map.of(usersList.get(0).getId(), usersList.get(0).getName()));
    }

    @Test
    void findUserByIdTest() throws Exception {
        mockMvc.perform(get("/users/1")
                        .content(objectMapper.writeValueAsString(Optional.of(userDto))))
                .andExpect(status().isOk());
    }

    @Test
    public void findUserByIdWithCorrectId() {
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(0));
        userService2.save(usersList.get(0));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        userService2.save(usersList.get(1));
        Assertions.assertEquals(userService2.findUserById(1), Optional.of(usersList.get(1)));
    }

    @Test
    public void findUserByIdWithIncorrectId() {
        Mockito.when(userRepository.findById(99))
                .thenReturn(Optional.empty());

        Assertions.assertEquals(userService2.findUserById(99), Optional.empty());
    }

    @Test
    public void findUserDtoByIdWithCorrectId() {
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));

        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(0));
        userService2.save(usersList.get(0));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));
        userService2.save(usersList.get(1));
        Assertions.assertEquals(userService2.findUserDtoById(1), UserMapper.toUserDto(usersList.get(1)));
    }

    @Test
    public void findUserDtoByIdWithIncorrectId() {
        Mockito.when(userRepository.findById(99))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService2.findUserDtoById(99));
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users")
                        .content(objectMapper.writeValueAsString(new ArrayList<>())))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserWithCorrectId() {
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        Assertions.assertDoesNotThrow(() -> userService2.deleteUserById(1));
    }

    @Test
    public void deleteUserWithIncorrectId() {
        Assertions.assertThrows(NotFoundException.class, () -> userService2.deleteUserById(99));
    }

    @Test
    void patchUserTest() throws Exception {
        mockMvc.perform(patch("/users")
                .content(objectMapper.writeValueAsString(userDto)));
        userDto.setName("updated2");
        when(userService.updateFields(anyInt(), any()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void updateFieldsUserWithNotUniqueEmail() {
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);

        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com1");
        Assertions.assertThrows(CustomValidateException.class, () ->
                userService2.updateFields(1, UserMapper.toUserDto(usersList.get(1))));
    }

    @Test
    public void updateFieldsUserWithIncorrectId() {
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.empty());
        User upd = usersList.get(1);
        upd.setId(1);
        Assertions.assertThrows(NotFoundException.class, () ->
                userService2.updateFields(1, UserMapper.toUserDto(usersList.get(1))));
    }

    @Test
    public void updateFieldsCorrectUser() {
        Mockito.when(userRepository.findAll())
                .thenReturn(usersList);
        Mockito.when(userRepository.findById(1))
                .thenReturn(Optional.of(usersList.get(1)));
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(usersList.get(1));

        User upd = usersList.get(1);
        upd.setId(1);
        upd.setEmail("testEmail@email.com3");
        Assertions.assertEquals(userService2.updateFields(1, UserMapper.toUserDto(upd)), UserMapper.toUserDto(upd));
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
