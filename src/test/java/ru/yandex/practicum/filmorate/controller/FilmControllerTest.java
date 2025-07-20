package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.Collection;

public class FilmControllerTest {
    Film film;
    FilmController fc;

    @BeforeEach
    public void prepareTests() {
        film = new Film(1L,"Film first", "Film first description",
                LocalDate.of(2025, 01, 01), 120, null);
    }

    @Test
    void testCreateFilm() {
        System.out.println("==== Проверка создания карточки фильма ==================================================");
        Assertions.assertEquals(1L, film.getId(), "Ошибка создания карточки фильма");
    }

    @Test
    void testNonEmptyFilmName() {
        System.out.println("==== Проверка пустого названия фильма ===================================================");
        Assertions.assertNotNull(film.getName(), "Название фильма не может быть пустым");
    }

    @Test
    void testFilmDescriptionMaximumLength() {
        System.out.println("==== Проверка длины описания фильма =====================================================");
        boolean isLengthLessThan200 = true;
        if (film.getDescription().length() > 200)
            isLengthLessThan200 = false;
        Assertions.assertTrue(isLengthLessThan200, "Длина описания больше 200 знаков");
    }

    @Test
    void testFilmReleaseDate() {
        System.out.println("==== Проверка даты релиза фильма ========================================================");
        boolean isReleaseDateGreatestThanCinemaBirthday = true;
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            isReleaseDateGreatestThanCinemaBirthday = false;
        Assertions.assertTrue(isReleaseDateGreatestThanCinemaBirthday, "Дата релиза раньше 28.12.1895");
    }

    @Test
    void testFilmPositiveDuration() {
        System.out.println("==== Проверка положительного значения продолжительности фильма ==========================");
        boolean isPositiveFilmDuration = true;
        if (film.getDuration() <= 0)
            isPositiveFilmDuration = false;
        Assertions.assertTrue(isPositiveFilmDuration, "Продолжительность фильма не является положительным числом");
    }

    @Test
    void testGetFilms() {
        System.out.println("==== Проверка получения карточек 3-х фильмов ============================================");
        //FilmController fc = new FilmController();
        InMemoryFilmStorage fc = new InMemoryFilmStorage();

        Film film2 = new Film(2L,"Film second", "Film second description",
                LocalDate.of(2020, 11, 21), 240, null);

        Film film3 = new Film();
        film3.setName("Film third");
        film3.setDescription("Film third description");
        film3.setReleaseDate(LocalDate.of(2005, 12, 28));
        film3.setDuration(100);

        fc.create(film);
        fc.create(film2);
        fc.create(film3);
        Collection<Film> films = fc.findAll();
        Assertions.assertEquals(3, films.size(), "Должно быть 3 фильма");
    }
}
