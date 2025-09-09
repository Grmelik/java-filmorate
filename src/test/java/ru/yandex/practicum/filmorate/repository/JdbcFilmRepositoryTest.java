package ru.yandex.practicum.filmorate.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("dev")
class JdbcFilmRepositoryTest {

    @Autowired
    private JdbcFilmRepository filmRepository;

    @Test
    @DisplayName("create/findById/update/delete flow works on H2")
    void crudFlow_ok() {
        Rating mpa = new Rating();
        mpa.setRatingId(5L);

        Film toCreate = Film.builder()
                .filmName("Matrix")
                .description("Neo")
                .releaseDate(LocalDate.of(1999, 3, 31))
                .duration(136)
                .mpa(mpa)
                .build();

        Film created = filmRepository.create(toCreate);
        assertThat(created.getFilmId()).isNotNull();

        Film found = filmRepository.findById(created.getFilmId()).orElseThrow();
        assertThat(found.getFilmName()).isEqualTo("Matrix");

        Film toUpdate = found.toBuilder().filmName("Matrix Reloaded").build();
        filmRepository.update(toUpdate);
        Film afterUpdate = filmRepository.findById(created.getFilmId()).orElseThrow();
        assertThat(afterUpdate.getFilmName()).isEqualTo("Matrix Reloaded");

        filmRepository.delete(created.getFilmId());
        assertThat(filmRepository.findById(created.getFilmId())).isEmpty();
    }
}


