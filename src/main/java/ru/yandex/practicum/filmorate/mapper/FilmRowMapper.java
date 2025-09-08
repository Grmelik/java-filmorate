package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating mpa = null;
        long ratingId = rs.getLong("rating_id"); //BEM,07.09.2025

        if (!rs.wasNull() && ratingId != 0) {
            mpa = new Rating(ratingId, rs.getString("rating_name"));
        }

        return new Film().toBuilder()
                .filmId(rs.getLong("film_id"))
                .filmName(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("releasedate").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .build();
    }
}
