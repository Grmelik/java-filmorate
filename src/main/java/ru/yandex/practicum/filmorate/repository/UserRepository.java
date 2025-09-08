package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserRepository {
    User create(User user);

    User update(User newUser);

    Collection<User> findAll();

    long getNextId();

    Optional<User> findById(Long userId);

    boolean delete(Long userId);
}
