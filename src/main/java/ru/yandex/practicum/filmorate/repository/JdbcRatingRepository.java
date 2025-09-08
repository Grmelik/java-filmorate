package ru.yandex.practicum.filmorate.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcRatingRepository extends BaseRepository<Rating> implements RatingRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM ratings ORDER BY rating_id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM ratings WHERE rating_id = :ratingId";

    public JdbcRatingRepository(NamedParameterJdbcOperations jdbc, RowMapper<Rating> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Rating> findAll() {
        return findMany(FIND_ALL_QUERY, new HashMap<>());
    }

    @Override
    public Optional<Rating> findById(Long ratingId) {
        Map<String, Object> params = new HashMap<>();
        params.put("ratingId", ratingId);
        return findOne(FIND_BY_ID_QUERY, params);
    }
}
