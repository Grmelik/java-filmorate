package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User newUser) {
        return userStorage.update(newUser);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление друзей.");
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь (друг) с id = " + friendId + " не найден.");
        }
        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);
        log.info("Пользователи {} и {} - друзья", userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Удаление из друзей");
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }

        User friend = userStorage.getUserById(friendId);
        if (friend == null) {
            throw new NotFoundException("Пользователь (друг) с id = " + friendId + " не найден.");
        }
        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);
        log.info("Пользователи {} и {} больше не друзья", userId, friendId);
    }

    public Collection<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден.");
        }
        return user.getFriendIds().stream()
                .filter(Objects::nonNull)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriendsList(Long userIdOne, Long userIdTwo) {
        User userOne = userStorage.getUserById(userIdOne);
        if (userOne == null) {
            throw new NotFoundException("Пользователь 1 с id = " + userIdOne + " не найден.");
        }

        User userTwo = userStorage.getUserById(userIdTwo);
        if (userTwo == null) {
            throw new NotFoundException("Пользователь 2 с id = " + userIdTwo + " не найден.");
        }

        return userOne.getFriendIds().stream()
                .filter(userTwo.getFriendIds()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
