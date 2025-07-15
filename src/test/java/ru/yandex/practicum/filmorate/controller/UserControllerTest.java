package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.LocalDate;
import java.util.Collection;

public class UserControllerTest {
    User user;
    UserController uc; // = new UserController();

    @BeforeEach
    public void prepareTests() {
        user = new User(1L, "user@test.com", "userone", "User One", LocalDate.of(1980, 03, 25), null);
    }

    @Test
    void testCreate() {
        System.out.println("==== Проверка создания пользователя =====================================================");
        User userView = uc.create(user);
        Assertions.assertEquals(1, userView.getId(), "Ошибка создания пользователя");

        System.out.println("==== Проверка пустой электронной почты ==================================================");
        Assertions.assertNotNull(userView.getEmail(), "Электронная почта не должна быть пустой");

        System.out.println("==== Проверка наличия символа @ у электронной почты =====================================");
        Assertions.assertTrue(userView.getEmail().contains("@"), "В электронной почте должен быть символ @");

        System.out.println("==== Проверка пустого логина пользователя ===============================================");
        Assertions.assertNotNull(userView.getLogin(), "Логин пользователя не должен быть пустым");

        System.out.println("==== Проверка даты рождения пользователя ================================================");
        boolean isBirthdayInFuture = false;
        if (LocalDate.now().isAfter(userView.getBirthday()))
            isBirthdayInFuture = true;
        Assertions.assertTrue(isBirthdayInFuture, "Дата рождения является будущей датой");
    }

    @Test
    void testGetUsers() {
        System.out.println("==== Проверка просмотра пользователей ===================================================");
        User user2 = new User(2L, "user2@test.ru", "usertwo", "User Two", LocalDate.of(1981, 03, 25), null);
        uc.create(user);
        uc.create(user2);
        Collection<User> users = uc.findAll();
        Assertions.assertEquals(2, users.size(), "Должно быть 2 пользователя");
    }
}
