package ru.yandex.practicum.filmorate.storage;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        log.info("Заведение карточки фильма {}", film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Заведена карточка фильма {}", film.getName());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обновление карточки фильма {} с id = {}", newFilm.getName(), newFilm.getId());
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
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Просмотр карточек фильмов.");
        return new ArrayList<>(films.values());
    }

    @Override
    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Film getFilmById(Long id) {
        return films.get(id);
    }
}
