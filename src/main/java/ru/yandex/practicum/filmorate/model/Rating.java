package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @JsonProperty("id")
    private Long ratingId;
    @JsonProperty("name")
    @NotBlank(message = "Название рейтинга не может быть пустым")
    private String ratingName;
}