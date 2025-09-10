package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Qualifier("friendRepository")
@RequiredArgsConstructor
public class JdbcFriendRepository implements FriendRepository {
    private static final String FIND_BY_ID_QUERY = """
            SELECT u.*
             FROM friends f
             JOIN users u ON f.friend_id = u.user_id
             WHERE f.user_id = :userId
             ORDER BY u.user_id
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO friends(user_id, friend_id, confirmed)
             VALUES (:userId, :friendId, TRUE)
            """;
    private static final String DELETE_QUERY = """
            DELETE FROM friends
             WHERE user_id = :userId and friend_id = :friendId
            """;
    private static final String GET_STATUS_QUERY = """
            SELECT COUNT(*)
             FROM friends
             WHERE user_id = :userId and friend_id = :friendId
            """;
    private static final String GET_COMMON_FRIENDS_QUERY = """
            SELECT u.*
             FROM users u
             JOIN friends f1 ON u.user_id = f1.friend_id
             JOIN friends f2 ON u.user_id = f2.friend_id
             WHERE f1.user_id = :userId1 AND f2.user_id = :userId2
             ORDER BY u.user_id
            """;

    private final NamedParameterJdbcOperations jdbc;
    private final UserRowMapper userRowMapper;

    @Override
    public void addFriend(Long userId, Long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        int statusConfirmed = jdbc.queryForObject(GET_STATUS_QUERY, params, Integer.class);
        if (statusConfirmed == 1) {
            params.put("confirmed", "TRUE");
        } else {
            params.put("confirmed", "FALSE");
        }
        jdbc.update(INSERT_QUERY, params);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        jdbc.update(DELETE_QUERY, params);
    }

    @Override
    public Collection<User> getFriends(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return jdbc.query(FIND_BY_ID_QUERY, params, userRowMapper);
    }

    @Override
    public Collection<User> getCommonFriendsList(Long userIdOne, Long userIdTwo) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId1", userIdOne);
        params.put("userId2", userIdTwo);
        return jdbc.query(GET_COMMON_FRIENDS_QUERY, params, userRowMapper);
    }

    @Override
    public boolean isFriend(Long userId, Long friendId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("friendId", friendId);
        Integer count = jdbc.queryForObject(GET_STATUS_QUERY, params, Integer.class);
        return count != null && count > 0;
    }
}
