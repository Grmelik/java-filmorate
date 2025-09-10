package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreService genreService;

    public FilmRowMapper(GenreService genreService) {
        this.genreService = genreService;
    }

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Rating mpa = null;
        long ratingId = rs.getLong("rating_id");

        if (!rs.wasNull() && ratingId != 0) {
            mpa = new Rating(ratingId, rs.getString("rating_name"));
        }

        Film film = Film.builder()
                .filmId(rs.getLong("film_id"))
                .filmName(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpa)
                .build();

        Set<Genre> genres = genreService.findByFilmId(film.getFilmId());
        film.setGenres(genres);

        return film;
    }
}
