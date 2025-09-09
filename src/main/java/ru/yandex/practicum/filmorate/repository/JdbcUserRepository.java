package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Qualifier("UserRepository")
public class JdbcUserRepository extends BaseRepository<User> implements UserRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM users ORDER BY user_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = :userId";
    private static final String INSERT_QUERY = """
            INSERT INTO users(login, user_name, email, birthday)
             VALUES (:login, :userName, :email, :birthday)
            """;
    private static final String UPDATE_QUERY = """
            UPDATE users
             SET login = :login, user_name = :userName, email = :email, birthday = :birthday
             WHERE user_id = :userId
            """;
    private static final String DELETE_QUERY = "DELETE FROM users WHERE user_id = :userId";
    private static final String GET_NEXT_ID_QUERY = "SELECT COALESCE(MAX(user_id), 0) + 1 FROM users";

    public JdbcUserRepository(NamedParameterJdbcOperations jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        validateUser(user);
        Map<String, Object> params = new HashMap<>();
        params.put("login", user.getLogin());
        params.put("userName", user.getUserName());
        params.put("email", user.getEmail());
        params.put("birthday", user.getBirthday());
        log.info("create, params={}", params);
        long id = insert(INSERT_QUERY, params);
        user.setUserId(id);
        return user;
    }

    @Override
    public User update(User newUser) {
        if (newUser.getUserId() == null) {
            throw new IllegalArgumentException("Id пользователя не может быть пустым");
        }

        Optional<User> userFound = findById(newUser.getUserId());
        if (userFound.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + newUser.getUserId() + " не найден.");
        }

        validateUser(newUser);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", newUser.getUserId());
        params.put("login", newUser.getLogin());
        params.put("userName", newUser.getUserName());
        params.put("email", newUser.getEmail());
        params.put("birthday", newUser.getBirthday());

        update(UPDATE_QUERY, params);
        return newUser;
    }

    @Override
    public Collection<User> findAll() {
        return findMany(FIND_ALL_QUERY, new HashMap<>());
    }

    @Override
    public long getNextId() {
        try {
            Map<String, Object> params = new HashMap<>();
            Long result = jdbc.queryForObject(GET_NEXT_ID_QUERY, params, Long.class);
            return result != null ? result : 1L;
        } catch (Exception e) {
            log.error("Error getting next ID", e);
            throw new InternalServerException("Не удалось получить следующий ID");
        }
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (userId == null) {
            return Optional.empty();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        return findOne(FIND_BY_ID_QUERY, params);
    }

    @Override
    public void delete(Long userId) {
        if (userId != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            delete(DELETE_QUERY, params);
        }
    }

    private void validateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("Надо заполнить сведения о пользователе");
        }
        if (user.getLogin() == null || user.getLogin().trim().isEmpty()) {
            throw new IllegalArgumentException("Логин пользователя не может быть пустым");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Электронная почта пользователя должна быть заполнена");
        }
        if (user.getBirthday() == null) {
            throw new IllegalArgumentException("День рождения пользователя должен быть заполнен");
        }
    }
}
