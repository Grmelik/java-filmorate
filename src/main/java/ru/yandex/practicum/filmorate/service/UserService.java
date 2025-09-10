package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.create(user);
    }

    public User update(User newUser) {
        return userRepository.update(newUser);
    }

    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        if (userId == null) {
            log.info("Id пользователя не может быть пустым.");
            throw new ValidationException("Id пользователя не может быть пустым.");
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id = " + userId));
    }
}
