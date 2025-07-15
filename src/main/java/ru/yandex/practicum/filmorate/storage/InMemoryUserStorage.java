package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        log.info("Заведение пользователя {}", user.getLogin());
        user.setId(getNextId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Создан пользователь с логином {}", user.getLogin());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление пользователя {} с id = {}", newUser.getLogin(), newUser.getId());
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            log.error("Пользователь с id = {} не найден.", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден.");
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
    }

    @Override
    public Collection<User> findAll() {
        log.info("Просмотр пользователей.");

        ArrayList<User> usersList = new ArrayList<>(users.values());
        if (usersList.isEmpty()) {
            throw new NotFoundException("Список пользователей пуст.");
        }
        return usersList;
    }

    @Override
    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public User getUserById(Long id) {
        return users.get(id);
    }
}
