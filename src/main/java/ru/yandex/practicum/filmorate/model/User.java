package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @NotBlank(message = "Электронная почта не может быть пустой")
    @Email(message = "Неверный формат электронной почты")
    private String email;
    @NotNull(message = "Логин не должен быть пустым")
    @NotBlank(message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
