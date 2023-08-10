package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User createUser(UserDto user) {
        checkUserName(user);
        return repository.createUser(user);
    }

    @Override
    public User updateUser(UserDto userDto, long id) {
        return repository.updateUser(userDto, id);
    }

    @Override
    public void deleteUser(long id) {
        repository.deleteUser(id);
    }

    @Override
    public User getUserById(long id) {
        Optional<User> user = repository.getUserById(id);
        return user.orElseThrow(() -> new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}"));
    }

    @Override
    public List<User> getUsers() {
        return repository.getUsers();
    }

    private void checkUserName(UserDto user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getEmail());
        }
    }
}
