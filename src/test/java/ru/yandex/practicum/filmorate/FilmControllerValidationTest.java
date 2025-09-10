package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class FilmControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /films with blank name returns 400")
    void createFilm_blankName_badRequest() throws Exception {
        String payload = "{" +
                "\"name\":\" \"," +
                "\"description\":\"desc\"," +
                "\"releaseDate\":\"1999-01-01\"," +
                "\"duration\":120" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /films with too early releaseDate returns 400")
    void createFilm_earlyReleaseDate_badRequest() throws Exception {
        String payload = "{" +
                "\"name\":\"Old\"," +
                "\"description\":\"desc\"," +
                "\"releaseDate\":\"1800-01-01\"," +
                "\"duration\":100" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /films with negative duration returns 400")
    void createFilm_negativeDuration_badRequest() throws Exception {
        String payload = "{" +
                "\"name\":\"Bad\"," +
                "\"description\":\"desc\"," +
                "\"releaseDate\":\"2000-01-01\"," +
                "\"duration\":-1" +
                "}";

        mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}


