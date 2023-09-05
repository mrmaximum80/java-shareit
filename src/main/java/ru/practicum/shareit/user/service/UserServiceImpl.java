package ru.practicum.shareit.user.service;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.map.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Data
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public User createUser(UserDto user) {
        checkUserName(user);
        return repository.save(UserMapper.toUser(user));
    }

    @Override
    @Transactional
    public User updateUser(UserDto userDto, long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return repository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public User getUserById(long id) {
        Optional<User> user = repository.findById(id);
        return user.orElseThrow(() -> new NotFoundException("{\"message\": \"Пользователь с id=" + id + " не найден\"}"));
    }

    @Override
    @Transactional
    public List<User> getUsers() {
        return repository.findAll();
    }

    private void checkUserName(UserDto user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getEmail());
        }
    }
}
