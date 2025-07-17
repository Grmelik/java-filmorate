package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        return filmStorage.update(newFilm);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка.");
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        film.getLikes().add(userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        log.info("Удаление лайка.");
        Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден.");
        }

        User user = userStorage.getUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден.");
        }
        film.getLikes().remove(userId);
        log.info("У фильма {} удален лайк от пользователя {}", filmId, userId);
    }

    public Collection<Film> getFilmsTop(int count) {
        log.info("Топ-{} популярных фильмов.", count);
        return filmStorage.findAll()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }
 }
