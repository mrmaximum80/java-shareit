package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService userService;

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto("user", "user@mail");
        User user = userService.createUser(userDto);

        assertNotNull(user.getId());
        assertEquals(user.getName(), "user");
        assertEquals(user.getEmail(), "user@mail");

        final DataIntegrityViolationException exception = assertThrows(

                DataIntegrityViolationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        User user = userService.createUser(userDto);
                    }
                });

//        UserDto userDto2 = new UserDto("", "user2@mail");
//        User user2 = userService.createUser(userDto2);
//
//        assertNotNull(user2.getId());
//        assertEquals(user2.getName(), "user2@mail");
//        assertEquals(user2.getEmail(), "user2@mail");
    }

    @Test
    void updateUserTest() {
        UserDto userDto = new UserDto("user", "user@mail");
        User user = userService.createUser(userDto);

        userDto.setName("updateUser");
        userDto.setEmail("updateUser@mail");

        long userId = user.getId();
        user = userService.updateUser(userDto, userId);

        assertEquals(user.getName(), "updateUser");
        assertEquals(user.getEmail(), "updateUser@mail");
    }

    @Test
    void deleteUser() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        UserService userService = new UserServiceImpl(userRepository);

        userService.deleteUser(1L);

        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(1L);

    }

    @Test
    void getUserByIdTest() {
        UserDto userDto = new UserDto("user", "user@mail");
        User user = userService.createUser(userDto);

        long userId = user.getId();

        User user2 = userService.getUserById(userId);
        assertEquals(user.getName(), user2.getName());
        assertEquals(user.getEmail(), user2.getEmail());

        final NotFoundException exception = assertThrows(

                NotFoundException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        User user = userService.getUserById(100);
                    }
                });
    }

    @Test
    void getUsers() {

        List<User> users = userService.getUsers();
        assertEquals(users.size(), 0);

        UserDto userDto = new UserDto("user1", "user1@mail");
        User user = userService.createUser(userDto);

        users = userService.getUsers();
        assertEquals(users.size(), 1);

        userDto = new UserDto("user2", "user2@mail");
        User user2 = userService.createUser(userDto);

        users = userService.getUsers();
        assertEquals(users.size(), 2);

    }
}
