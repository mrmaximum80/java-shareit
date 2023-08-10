package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    User createUser(UserDto user);

    User updateUser(UserDto userDto, long id);

    void deleteUser(long id);

    User getUserById(long id);

    List<User> getUsers();

}
