package ru.practicum.shareit;


import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest {


    @Test
    public void createNewUser() {
        UserDto userDto = new UserDto();
        userDto.setName("test");
        userDto.setEmail("testEmail@email.com");

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        Mockito.when(userRepository.save(Mockito.any()))
                .thenReturn(userDto);

        UserService userService = new UserService(userRepository);
        Assertions.assertEquals(userService.save(userDto), userDto);

    }
}
