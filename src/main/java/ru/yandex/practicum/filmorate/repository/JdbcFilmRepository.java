package ru.yandex.practicum.filmorate.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Repository
@Qualifier("FilmRepository")
public class JdbcFilmRepository extends BaseRepository<Film> implements FilmRepository {
    private static final String FIND_ALL_QUERY =
            "SELECT f.film_id, f.film_name, f.description, f.releasedate, f.duration, r.rating_id, r.rating_name" +
            " FROM films f" +
            " LEFT JOIN ratings r ON f.rating_id = r.rating_id" +
            " ORDER BY f.film_id";
    private static final String FIND_BY_ID_QUERY =
            "SELECT f.film_id, f.film_name, f.description, f.releasedate, f.duration, r.rating_id, r.rating_name" +
            " FROM films f" +
            " LEFT JOIN ratings r ON f.rating_id = r.rating_id" +
            " WHERE f.film_id = :filmId";
    private static final String FIND_FILMS_TOP =
            "SELECT f.film_id, f.film_name, f.description, f.releasedate, f.duration, r.rating_id, r.rating_name," +
            " count(l.user_id) cnt" +
            " FROM films f" +
            " JOIN ratings r ON f.rating_id = r.rating_id" +
            " LEFT JOIN likes l ON f.film_id = l.film_id" +
            " GROUP BY f.film_id, f.film_name, f.description, f.releasedate, f.duration, r.rating_id, r.rating_name" +
            " ORDER BY cnt DESC, releasedate" +
            " LIMIT :count";
    private static final String INSERT_QUERY =
            "INSERT INTO films(film_name, description, releasedate, duration, rating_id)" +
            " VALUES (:filmName, :description, :releasedate, :duration, :ratingId)";
    private static final String UPDATE_QUERY =
            "UPDATE films" +
            " SET film_name = :filmName, description = :description, releasedate = :releasedate," +
            " duration = :duration, rating_id = :ratingId" +
            " WHERE film_id = :filmId";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE film_id = :filmId";
    private static final String GET_NEXT_ID_QUERY = "SELECT COALESCE(MAX(film_id), 0) + 1 FROM films";
    private static final String FILL_FILM_GENRES = "INSERT INTO film_genres(film_id, genre_id) VALUES(?, ?)";
    private static final String CLEAN_FILM_GENRES = "DELETE from film_genres WHERE film_id = :filmId";

    private final GenreRepository genreRepository;

    public JdbcFilmRepository(NamedParameterJdbcOperations jdbc, RowMapper<Film> mapper, GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
    }

    @Override
    public Film create(Film film) {
        validateFilm(film);

        Map<String, Object> params = new HashMap<>();
        params.put("filmName", film.getFilmName());
        params.put("description", film.getDescription());
        params.put("releasedate", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("ratingId", film.getMpa() != null ? film.getMpa().getRatingId() : null);

        long filmId = insert(INSERT_QUERY, params);
        film.setFilmId(filmId);
        fillGenresTable(film.getFilmId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getFilmId() == null) {
            throw new IllegalArgumentException("Id фильма не может быть пустым");
        }

        Optional<Film> filmFound = findById(newFilm.getFilmId());
        if (filmFound.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + newFilm.getFilmId() + " не найден.");
        }
        validateFilm(newFilm);

        Map<String, Object> params = new HashMap<>();
        params.put("filmId", newFilm.getFilmId());
        params.put("filmName", newFilm.getFilmName());
        params.put("description", newFilm.getDescription());
        params.put("releasedate", newFilm.getReleaseDate());
        params.put("duration", newFilm.getDuration());
        params.put("ratingId", newFilm.getMpa() != null ? newFilm.getMpa().getRatingId() : null);

        update(UPDATE_QUERY, params);
        fillGenresTable(newFilm.getFilmId(), newFilm.getGenres());
        return newFilm;
    }

    @Override
    public Collection<Film> findAll() {
        try {
            return findMany(FIND_ALL_QUERY, new HashMap<>());
        } catch (Exception e) {
            log.error("Data not found.", e);
            throw new NotFoundException("Данные не найдены.");
        }
    }

    @Override
    public Collection<Film> getFilmsTop(int count) {
        Map<String, Object> params = new HashMap<>();
        params.put("count", count);
        return findMany(FIND_FILMS_TOP, params);
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
    public Optional<Film> findById(Long filmId) {
        if (filmId == null) {
            return Optional.empty();
        }

        log.debug("Executing SQL: {} with filmId: {}", FIND_BY_ID_QUERY, filmId);

        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        //return findOne(FIND_BY_ID_QUERY, params);
        try {
            return findOne(FIND_BY_ID_QUERY, params)
                    .map(film -> {
                        film.setGenres(genreRepository.findByFilmId(filmId));
                        return film;
                    });
        } catch (DataAccessException e) {
            log.error("SQL error: {}", e.getMessage());
            throw new RuntimeException("Database error", e);
        }
    }

    @Override
    public boolean delete(Long filmId) {
        if (filmId == null) {
            return false;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return delete(DELETE_QUERY, params);
    }

    public void fillGenresTable(Long filmId, Set<Genre> genres) {
        if (filmId != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("filmId", filmId);
            jdbc.update(CLEAN_FILM_GENRES, params);
            List<Genre> genreList = new ArrayList<>(genres);
            jdbc.getJdbcOperations().batchUpdate(
                    FILL_FILM_GENRES,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, filmId);
                            ps.setInt(2, Math.toIntExact(genreList.get(i).getGenreId()));
                        }

                        @Override
                        public int getBatchSize() {
                            return genreList.size();
                        }
                    }
                    );
        }
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Фильм не может быть пустым");
        }
        if (film.getFilmName() == null || film.getFilmName().trim().isEmpty()) {
            throw new IllegalArgumentException("Наименование фильма не может быть пустым");
        }
        if (film.getReleaseDate() == null) {
            throw new IllegalArgumentException("Дата релиза не может быть пустой");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new IllegalArgumentException("Продолжительность фильма должна быть больше 0");
        }
        if (film.getMpa() == null) {
            throw new IllegalArgumentException("Рейтинг не может быть пустым");
        }
    }
}
