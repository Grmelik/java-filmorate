package ru.yandex.practicum.filmorate.tools;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.annotation.ReleaseDate;

import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, LocalDate> {
    private LocalDate releaseDate;

    @Override
    public void initialize(ReleaseDate constraintAnnotation) {
        this.releaseDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(releaseDate);
    }
}
