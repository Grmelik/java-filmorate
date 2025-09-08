package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Genre {
    @JsonProperty("id")
    @NotNull(message = "ID жанра не может быть null")
    private Long genreId;
    @JsonProperty("name")
    @NotBlank(message = "Название жанра не может быть пустым")
    private String genreName;
}
