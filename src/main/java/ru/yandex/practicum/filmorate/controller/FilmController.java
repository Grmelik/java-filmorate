package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Заведение карточки фильма {}", film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Заведена карточка фильма {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.debug("Обновление карточки фильма {}", newFilm.getName());
        try {
            Film oldFilm = films.get(newFilm.getId());
            if (oldFilm == null) {
                log.error("Фильм с id = {} не найден.", newFilm.getId());
                throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден.");
            }
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Карточка фильма {} обновлена.", oldFilm.getName());
            return oldFilm;
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении карточки фильма");
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Просмотр карточек фильмов.");
        return new ArrayList<>(films.values());
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
