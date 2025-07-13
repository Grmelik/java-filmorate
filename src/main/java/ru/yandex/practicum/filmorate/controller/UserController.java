package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Заведение пользователя {}", user.getLogin());
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Создан пользователь с логином {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.debug("Обновление пользователя {}", newUser.getLogin());
        try {
            User oldUser = users.get(newUser.getId());
            if (oldUser == null) {
                log.error("Пользователь с id = {} не найден.", oldUser.getId());
                throw new NotFoundException("Пользователь с id = " + oldUser.getId() + " не найден.");
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь с логином {} обновлен.", oldUser.getLogin());
            return oldUser;
        } catch (RuntimeException e) {
            log.error("Ошибка обновления пользователя");
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Просмотр пользователей.");
        return new ArrayList<>(users.values());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
