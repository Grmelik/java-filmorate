package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        try {
            if (user.getEmail() == null || user.getEmail().isBlank()) {
                log.warn("Электронная почта не может быть пустой.");
                throw new ValidationException("Электронная почта не может быть пустой.");
            }
            if (!user.getEmail().contains("@")) {
                log.warn("Неверная электронная почта {}", user.getEmail());
                throw new ValidationException("Электронная почта должна содержать символ @.");
            }
            if (user.getLogin() == null || user.getLogin().isBlank()) {
                log.warn("Логин не должен быть пустым.");
                throw new ValidationException("Логин не должен быть пустым.");
            }
            if (user.getLogin().indexOf(" ") > 0) {
                log.warn("Логин {} не должен содержать пробелы.", user.getLogin());
                throw new ValidationException("Логин не должен содержать пробелы.");
            }
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Дата рождения {} является будущей датой.", user.getBirthday());
                throw new ValidationException("Дата рождения не может быть в будущем.");
            }
        } catch (Exception e) {
            log.error("Ошибка при создании пользователя ", e);
            throw e;
        }
        user.setId(getNextId());
        log.debug("=== СОЗДАНИЕ ПОЛЬЗОВАТЕЛЯ =========================================================================");
        log.debug("id = {}", user.getId());
        log.debug("email = {}", user.getEmail());
        log.debug("login = {}", user.getLogin());
        log.debug("name = {}", user.getName());
        log.debug("birthday = {}", user.getBirthday());
        log.debug("===================================================================================================");
        users.put(user.getId(), user);
        log.info("Создан пользователь с логином {}", user.getLogin());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        try {
            if (newUser.getId() == null) {
                log.warn("Id должен быть указан.");
                throw new ValidationException("Id должен быть указан.");
            }
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
                log.warn("Электронная почта не может быть пустой.");
                throw new ValidationException("Электронная почта не может быть пустой.");
            }
            if (!newUser.getEmail().contains("@")) {
                log.warn("Неверная электронная почта {}", newUser.getEmail());
                throw new ValidationException("Электронная почта должна содержать символ @.");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank()) {
                log.warn("Логин не должен быть пустым.");
                throw new ValidationException("Логин не должен быть пустым.");
            }
            if (newUser.getLogin().indexOf(" ") > 0) {
                log.warn("Логин {} не должен содержать пробелы.", newUser.getLogin());
                throw new ValidationException("Логин не должен содержать пробелы.");
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                oldUser.setName(newUser.getLogin());
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.debug("=== ОБНОВЛЕНИЕ ПОЛЬЗОВАТЕЛЯ ===================================================================");
            log.debug("id = {}", oldUser.getId());
            log.debug("email = {}", oldUser.getEmail());
            log.debug("login = {}", oldUser.getLogin());
            log.debug("name = {}", oldUser.getName());
            log.debug("birthday = {}", oldUser.getBirthday());
            log.debug("===============================================================================================");
            log.info("Пользователь с логином {} обновлен.", oldUser.getLogin());
            return oldUser;
        } catch (NotFoundException e) {
            log.error("Пользователь с id = {} не найден.", newUser.getId());
            throw e;
        }
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Просмотр пользователей.");
        return users.values();
    }
}
