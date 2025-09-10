package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class JdbcLikeRepository implements LikeRepository {
    private static final String INSERT_QUERY = "INSERT INTO likes(user_id, film_id) VALUES (:userId, :filmId)";
    private static final String DELETE_QUERY = "DELETE FROM likes WHERE user_id = :userId and film_id = :filmId";
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public void addLike(Long filmId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("filmId", filmId);
        jdbc.update(INSERT_QUERY, params);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("filmId", filmId);
        jdbc.update(DELETE_QUERY, params);
    }
}
