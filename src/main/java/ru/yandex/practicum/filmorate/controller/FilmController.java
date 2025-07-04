package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    private final LocalDate birthdayCinema = LocalDate.of(1895, 12, 28);

    private boolean isPositiveDuration(Integer duration) {
        if (duration > 0)
            return true;
        return false;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        try {
            if (film.getName() == null || film.getName().isBlank()) {
                log.warn("Название фильма не может быть пустым.");
                throw new ValidationException("Название фильма не может быть пустым.");
            }
            if (film.getDescription().length() > 200) {
                log.warn("Длина описания равна {} и превышает 200 символов", film.getDescription().length());
                throw new ValidationException("Максимальная длина описания 200 символов.");

            }
            if (film.getReleaseDate().isBefore(birthdayCinema)) {
                log.warn("Дата релиза равна {}, она не может быть раньше 28 декабря 1895 года.", film.getReleaseDate());
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года.");
            }
            if (!isPositiveDuration(film.getDuration())) {
                log.warn("Продолжительность фильма не является положительным числом = {}.", film.getDuration());
                throw new ValidationException("Продолжительность фильма должна быть положительным числом.");
            }
        } catch (Exception e) {
            log.error("Ошибка при создании карточки фильма ", e);
            throw e;
        }
        film.setId(getNextId());
        log.debug("=== СОЗДАНИЕ КАРТОЧКИ ФИЛЬМА ======================================================================");
        log.debug("id = {}", film.getId());
        log.debug("name = {}", film.getName());
        log.debug("description = {}", film.getDescription());
        log.debug("releaseDate = {}", film.getReleaseDate());
        log.debug("duration = {}", film.getDuration());
        log.debug("===================================================================================================");
        films.put(film.getId(), film);
        log.info("Заведена карточка фильма {}", film.getName());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        try {
            if (newFilm.getId() == null) {
                log.warn("Id должен быть указан.");
                throw new NotFoundException("Id должен быть указан.");
            }
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.warn("Название фильма не может быть пустым.");
                throw new ValidationException("Название фильма не может быть пустым.");
            }
            if (newFilm.getDescription().length() > 200) {
                log.warn("Длина описания равна {} и превышает 200 символов", newFilm.getDescription().length());
                throw new ValidationException("Максимальная длина описания 200 символов.");
            }
            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.debug("=== ОБНОВЛЕНИЕ КАРТОЧКИ ФИЛЬМА ================================================================");
            log.debug("id = {}", oldFilm.getId());
            log.debug("name = {}", oldFilm.getName());
            log.debug("description = {}", oldFilm.getDescription());
            log.debug("releaseDate = {}", oldFilm.getReleaseDate());
            log.debug("duration = {}", oldFilm.getDuration());
            log.debug("===============================================================================================");
            log.info("Карточка фильма {} обновлена.", oldFilm.getName());
            return oldFilm;
        } catch (NotFoundException e) {
            log.error("Фильм с id = {} не найден.", newFilm.getId());
            throw e;
        }
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Просмотр карточек фильмов.");
        return films.values();
    }
}
