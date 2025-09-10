package ru.yandex.practicum.filmorate.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseRepository<T> {
    protected final NamedParameterJdbcOperations jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Map<String, ?> params) {
        try {
            T result = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Map<String, ?> params) {
        try {
            return jdbc.query(query, params, mapper);
        } catch (DataAccessException e) {
            log.error("Ошибка выполнения запроса: {}", query, e);
            Throwable rootCause = e.getRootCause();
            if (rootCause != null) {
                log.info("Root cause: {}", rootCause.getMessage());
            }
        }
        return List.of();
    }

    protected long insert(String query, Map<String, ?> params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(query, new MapSqlParameterSource(params), keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

     protected void update(String query, Map<String, ?> params) {
        int rowsUpdated = 0;
        try {
            rowsUpdated = jdbc.update(query, params);
        } catch (DataAccessException e) {
            log.error("Ошибка обновления данных при выполнении запроса: {}", query, e);
            Throwable rootCause = e.getRootCause();
            if (rootCause != null) {
                log.info("Root cause update: {}", rootCause.getMessage());
            }
        }
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected void delete(String query, Map<String, ?> params) {
        int rowsDeleted = 0;
        try {
            rowsDeleted = jdbc.update(query, params);
        } catch (DataAccessException e) {
            log.error("Ошибка удаления данных: {}", query, e);
            Throwable rootCause = e.getRootCause();
            if (rootCause != null) {
                log.info("Root cause delete: {}", rootCause.getMessage());
            }
        }
        if (rowsDeleted == 0) {
            throw new InternalServerException("Не удалось удалить данные");
        }
    }
}
