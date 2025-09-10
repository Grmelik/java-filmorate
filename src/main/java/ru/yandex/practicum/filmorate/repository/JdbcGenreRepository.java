package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Repository
public class JdbcGenreRepository extends BaseRepository<Genre> implements GenreRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY genre_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE genre_id = :genreId";
    private static final String FIND_GENRES_OF_FILM = """
            SELECT g.genre_id, g.genre_name
             FROM genres g
             JOIN film_genres fg
             ON g.genre_id = fg.genre_id
             WHERE fg.film_id = :filmId
             ORDER by g.genre_id
            """;

    public JdbcGenreRepository(NamedParameterJdbcOperations jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY, new HashMap<>());
    }

    @Override
    public Optional<Genre> findById(Long genreId) {
        Map<String, Object> params = new HashMap<>();
        params.put("genreId", genreId);
        return findOne(FIND_BY_ID_QUERY, params);
    }

    @Override
    public Set<Genre> findByFilmId(Long filmId) {
        Map<String, Object> params = new HashMap<>();
        params.put("filmId", filmId);
        return findMany(FIND_GENRES_OF_FILM, params).stream()
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparingLong(Genre::getGenreId))));
    }
}
