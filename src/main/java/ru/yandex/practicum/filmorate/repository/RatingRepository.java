package ru.yandex.practicum.filmorate.repository;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

public interface RatingRepository {
    List<Rating> findAll();

    Optional<Rating> findById(Long ratingId);
}
