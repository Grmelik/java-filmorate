package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.repository.FriendRepository;
import ru.yandex.practicum.filmorate.repository.UserRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public void addFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        log.info("Добавление пользователя {} в друзья пользователю {}", friendId, userId);
        friendRepository.addFriend(userId, friendId);
    }

    public void deleteFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        log.info("Удаление пользователя {} из друзей пользователя {}", friendId, userId);
        friendRepository.deleteFriend(userId, friendId);
        log.info("Пользователь {} больше не в друзьях у пользователя {}", friendId, userId);
    }

    public Collection<User> getFriends(Long userId) {
        validateUserExists(userId);
        log.info("Получение списка друзей пользователя {}", userId);
        return friendRepository.getFriends(userId);
    }

    public Collection<User> getCommonFriendsList(Long userIdOne, Long userIdTwo) {
        validateUsersExist(userIdOne, userIdTwo);
        log.info("Общие друзья пользователей {} и {}", userIdOne, userIdTwo);
        return friendRepository.getCommonFriendsList(userIdOne, userIdTwo);
    }

    public boolean isFriend(Long userId, Long friendId) {
        validateUsersExist(userId, friendId);
        log.info("Проверка дружбы между пользователями {} и {}", userId, friendId);
        return friendRepository.isFriend(userId, friendId);
    }

    private void validateUserExists(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ID пользователя не может быть null");
        }
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + userId + " не найден");
        }
    }

    private void validateUsersExist(Long userId, Long friendId) {
        if (userId == null || friendId == null) {
            throw new IllegalArgumentException("ID пользователей не могут быть null");
        }
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("Пользователь не может добавить себя в друзья");
        }
        validateUserExists(userId);
        validateUserExists(friendId);
    }
}
