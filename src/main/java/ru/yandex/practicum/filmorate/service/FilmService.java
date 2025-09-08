package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DatabaseConstraintException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.repository.FilmRepository;
import ru.yandex.practicum.filmorate.repository.LikeRepository;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final LikeRepository likeRepository;
    private final RatingService ratingService;

    public Film create(Film film) {
        if (film == null) {
            throw new IllegalArgumentException("Film cannot be null");
        }

        if (film.getMpa().getRatingId() == null) {
            film.setMpa(ratingService.findById(1L));
        }

        try {
            return filmRepository.create(film);
        } catch (DataIntegrityViolationException e) {
            log.error("Ошибка с констрейнтами: ", e.getMessage());
            if (e.getMessage().contains("FK_")) {
                throw new DatabaseConstraintException("Указанный рейтинг не существует.");
            }
            throw new DatabaseConstraintException("Ошибка при создании фильма: ", e.getCause());
        } catch (Exception e) {
            log.error("Ошибка: ", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Film update(Film newFilm) {
        try {
            return filmRepository.update(newFilm);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("FK_")) {
                throw new DatabaseConstraintException("Указанный рейтинг не существует.");
            }
            throw new DatabaseConstraintException("Ошибка при обновлении фильма: ", e.getCause());
        }
    }

    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    public void addLike(Long filmId, Long userId) {
        likeRepository.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        likeRepository.deleteLike(filmId, userId);
        log.info("У фильма {} удален лайк от пользователя {}", filmId, userId);
    }

    public Collection<Film> getFilmsTop(int count) {
        log.info("Топ-{} популярных фильмов.", count);
        try {
            return filmRepository.getFilmsTop(count);
        } catch (Exception e) {
            log.error("Data not found!", e);
            throw new NotFoundException("Данные не найдены!");
        }
    }

    public Film findById(Long filmId) {
        return filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + filmId + " не найден"));
    }
 }
