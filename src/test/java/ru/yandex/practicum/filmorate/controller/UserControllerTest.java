package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static final String E_MAIL_FIRST = RandomStringUtils.randomAlphabetic(10) + "@" + RandomStringUtils.randomAlphabetic(10) + ".com";
    private static final String E_MAIL_SECOND = RandomStringUtils.randomAlphabetic(12) + "@" + RandomStringUtils.randomAlphabetic(12) + ".com";
    private static final String LOGIN_FIRST = RandomStringUtils.randomAlphabetic(10);
    private static final String LOGIN_SECOND = RandomStringUtils.randomAlphabetic(12);
    private static final String NAME_FIRST = RandomStringUtils.randomAlphabetic(10);
    private static final String NAME_SECOND = RandomStringUtils.randomAlphabetic(12);
    private static final LocalDate BIRTHDAY_FIRST = LocalDate.of(1988, 1, 1);
    private static final LocalDate BIRTHDAY_SECOND = LocalDate.of(1999, 12, 31);

    @Test
    void getAllUsers() {
        UserController userController = new UserController();

        User userFirst = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userSecond = User.builder()
                .email(E_MAIL_SECOND)
                .login(LOGIN_SECOND)
                .name(NAME_SECOND)
                .birthday(BIRTHDAY_SECOND)
                .build();

        User userCreatedFirst = userController.createUser(userFirst);
        User userCreatedSecond = userController.createUser(userSecond);

        List<User> expectedUsers = new ArrayList<>(List.of(userCreatedFirst, userCreatedSecond));

        List<User> actualUsers = userController.getAllUsers();
        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void createUser() {
        UserController userController = new UserController();

        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userCreated = userController.createUser(user);

        assertEquals(user, userCreated);
    }

    @Test
    void createUserWithEmptyName() {
        UserController userController = new UserController();

        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name("")
                .birthday(BIRTHDAY_FIRST)
                .build();

        User createdUser = userController.createUser(user);

        assertEquals(LOGIN_FIRST, createdUser.getName());
    }

    @Test
    void updateUser() {
        UserController userController = new UserController();

        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userCreated = userController.createUser(user);
        userCreated.setLogin(LOGIN_SECOND);
        userCreated.setEmail(E_MAIL_SECOND);
        userCreated.setName(NAME_SECOND);
        userCreated.setBirthday(BIRTHDAY_SECOND);
        User userUpdated = userController.updateUser(userCreated);

        assertEquals(userCreated, userUpdated);
    }

    @Test
    void updateWrongUser() {
        UserController userController = new UserController();

        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userCreated = userController.createUser(user);
        userCreated.setName(NAME_FIRST + "_updated");
        userCreated.setId(-1);
        userCreated.setLogin(LOGIN_SECOND);
        userCreated.setEmail(E_MAIL_SECOND);
        userCreated.setName(NAME_SECOND);
        userCreated.setBirthday(BIRTHDAY_SECOND);

        final ValidationException[] validationException = new ValidationException[1];
        assertAll(
                () -> validationException[0] = assertThrows(ValidationException.class, () -> userController.updateUser(userCreated)),
                () -> assertEquals("Пользователь c id: -1 не существует", validationException[0].getMessage(), MessageFormat.format("Проверка сообщения об ошибке для кейса: {0}", "id: -1"))
        );
    }


    @ParameterizedTest
    @MethodSource("testDataProvider")
    void validateUserTest(TestData testData) {
        UserController userController = new UserController();
        final ValidationException[] validationException = new ValidationException[1];
        assertAll(
                () -> validationException[0] = assertThrows(ValidationException.class, () -> userController.createUser(testData.getUser()), testData.checkName),
                () -> assertEquals(testData.expectedMessage, validationException[0].getMessage(), MessageFormat.format("Проверка сообщения об ошибке для кейса: {0}", testData.checkName))
        );
    }

    private static Stream<TestData> testDataProvider() {
        return Stream.of(
                TestData.builder()
                        .user(User.builder()
                                .email("")
                                .login(LOGIN_FIRST)
                                .name(NAME_FIRST)
                                .birthday(BIRTHDAY_FIRST)
                                .build())
                        .expectedMessage("Электронная почта не может быть пустой и должна содержать символ @")
                        .checkName("Электронная почта пустая")
                        .build(),
                TestData.builder()
                        .user(User.builder()
                                .email("test.ru")
                                .login(LOGIN_FIRST)
                                .name(NAME_FIRST)
                                .birthday(BIRTHDAY_FIRST)
                                .build())
                        .expectedMessage("Электронная почта не может быть пустой и должна содержать символ @")
                        .checkName("Электронная почта не содержит символ @")
                        .build(),
                TestData.builder()
                        .user(User.builder()
                                .email(E_MAIL_FIRST)
                                .login("")
                                .name(NAME_FIRST)
                                .birthday(BIRTHDAY_FIRST)
                                .build())
                        .expectedMessage("Логин не может быть пустым и содержать пробелы")
                        .checkName("Логин пустой")
                        .build(),
                TestData.builder()
                        .user(User.builder()
                                .email(E_MAIL_FIRST)
                                .login(LOGIN_FIRST + " ")
                                .name(NAME_FIRST)
                                .birthday(BIRTHDAY_FIRST)
                                .build())
                        .expectedMessage("Логин не может быть пустым и содержать пробелы")
                        .checkName("Логин с пробелом")
                        .build(),
                TestData.builder()
                        .user(User.builder()
                                .email(E_MAIL_FIRST)
                                .login(LOGIN_FIRST)
                                .name(NAME_FIRST)
                                .birthday(LocalDate.now().plusDays(1))
                                .build())
                        .expectedMessage("Дата рождения не может быть в будущем")
                        .checkName("Дата рождения из будущего")
                        .build(),
                TestData.builder()
                        .user(null)
                        .expectedMessage("Пользователь не может быть null")
                        .checkName("Пользователь is null")
                        .build()
        );
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class TestData {
        String checkName;
        User user;
        String expectedMessage;
    }
}