package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmRepository {
    Film create(Film film);

    Film update(Film newFilm);

    Collection<Film> findAll();

    Collection<Film> getFilmsTop(int count);

    long getNextId();

    Optional<Film> findById(Long filmId);

    boolean delete(Long filmId);
}
