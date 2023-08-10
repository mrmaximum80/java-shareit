package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User createUser(UserDto user);

    User updateUser(UserDto userDto, long id);

    void deleteUser(long id);

    Optional<User> getUserById(long id);

    List<User> getUsers();
}
