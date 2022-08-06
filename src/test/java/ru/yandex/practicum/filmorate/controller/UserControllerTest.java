package ru.yandex.practicum.filmorate.controller;

import lombok.*;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final UserController userController;

    private static final String E_MAIL_FIRST = RandomStringUtils.randomAlphabetic(10) + "@" + RandomStringUtils.randomAlphabetic(10) + ".com";
    private static final String E_MAIL_SECOND = RandomStringUtils.randomAlphabetic(12) + "@" + RandomStringUtils.randomAlphabetic(12) + ".com";
    private static final String E_MAIL_COMMON = RandomStringUtils.randomAlphabetic(14) + "@" + RandomStringUtils.randomAlphabetic(14) + ".com";
    private static final String LOGIN_FIRST = RandomStringUtils.randomAlphabetic(10);
    private static final String LOGIN_SECOND = RandomStringUtils.randomAlphabetic(12);
    private static final String LOGIN_COMMON = RandomStringUtils.randomAlphabetic(14);
    private static final String NAME_FIRST = RandomStringUtils.randomAlphabetic(10);
    private static final String NAME_SECOND = RandomStringUtils.randomAlphabetic(12);
    private static final String NAME_COMMON = RandomStringUtils.randomAlphabetic(14);
    private static final LocalDate BIRTHDAY_FIRST = LocalDate.of(1988, 1, 1);
    private static final LocalDate BIRTHDAY_SECOND = LocalDate.of(1999, 12, 31);
    private static final LocalDate BIRTHDAY_COMMON = LocalDate.of(1994, 6, 15);


    @AfterEach
    void tearDown() {
        List<User> users = userController.getAllUsers();
        for (User user : users){
            userController.deleteUser(user.getId());
        }
    }

    @Test
    void getAllUsersTest() {
        User firstUser = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User secondUser = User.builder()
                .email(E_MAIL_SECOND)
                .login(LOGIN_SECOND)
                .name(NAME_SECOND)
                .birthday(BIRTHDAY_SECOND)
                .build();

        User firstCreatedUser = userController.createUser(firstUser);
        User secondCreatedUser = userController.createUser(secondUser);

        List<User> expectedUsers = Arrays.asList(firstCreatedUser, secondCreatedUser);
        List<User> actualUsers = userController.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
    }


    @Test
    void createUserTest() {
        User expectedUser = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User actualUser = userController.createUser(expectedUser);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserTest() {
        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User createdUser = userController.createUser(user);

        User actualUser = userController.getUser(createdUser.getId()).get();

        assertEquals(createdUser, actualUser);
    }

    @Test
    void addFriendsTest() {
        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User createdUser = userController.createUser(user);

        User friendUser = User.builder()
                .email(E_MAIL_SECOND)
                .login(LOGIN_SECOND)
                .name(NAME_SECOND)
                .birthday(BIRTHDAY_SECOND)
                .build();

        User expectedFriend = userController.createUser(friendUser);

        userController.addFriends(createdUser.getId(), expectedFriend.getId());
        List<User> friends = userController.getFriends(createdUser.getId());

        assertTrue(friends.contains(expectedFriend));
    }


    @Test
    void removeFriendsTest() {
        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User createdUser = userController.createUser(user);

        User friendUser = User.builder()
                .email(E_MAIL_SECOND)
                .login(LOGIN_SECOND)
                .name(NAME_SECOND)
                .birthday(BIRTHDAY_SECOND)
                .build();

        User expectedFriend = userController.createUser(friendUser);

        userController.addFriends(createdUser.getId(), expectedFriend.getId());
        userController.removeFriends(createdUser.getId(), expectedFriend.getId());

        List<User> friends = userController.getFriends(createdUser.getId());

        assertFalse(friends.contains(expectedFriend));
    }


    @Test
    void getCommonFriendsTest() {
        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User createdUser = userController.createUser(user);

        User friendUser = User.builder()
                .email(E_MAIL_SECOND)
                .login(LOGIN_SECOND)
                .name(NAME_SECOND)
                .birthday(BIRTHDAY_SECOND)
                .build();

        User friendCreatedUser = userController.createUser(friendUser);

        User commonFriendUser = User.builder()
                .email(E_MAIL_COMMON)
                .login(LOGIN_COMMON)
                .name(NAME_COMMON)
                .birthday(BIRTHDAY_COMMON)
                .build();

        User commonFriendCreatedUser = userController.createUser(commonFriendUser);

        userController.addFriends(createdUser.getId(), commonFriendCreatedUser.getId());
        userController.addFriends(commonFriendCreatedUser.getId(), createdUser.getId());
        userController.addFriends(commonFriendCreatedUser.getId(), friendCreatedUser.getId());
        userController.addFriends(friendCreatedUser.getId(), commonFriendCreatedUser.getId());

        List<User> friends = userController.getCommonFriends(createdUser.getId(), friendCreatedUser.getId());

        assertTrue(friends.contains(commonFriendCreatedUser));
    }

    @Test
    void getCommonFriendsEmptyTest() {
        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userCreated = userController.createUser(user);

        User friend = User.builder()
                .email(E_MAIL_SECOND)
                .login(LOGIN_SECOND)
                .name(NAME_SECOND)
                .birthday(BIRTHDAY_SECOND)
                .build();

        User friendCreated = userController.createUser(friend);

        User commonFriend = User.builder()
                .email(E_MAIL_COMMON)
                .login(LOGIN_COMMON)
                .name(NAME_COMMON)
                .birthday(BIRTHDAY_COMMON)
                .build();

        User commonFriendCreated = userController.createUser(commonFriend);

        List<User> friends = userController.getCommonFriends(userCreated.getId(), friendCreated.getId());

        assertFalse(friends.contains(commonFriendCreated));
    }

    @Test
    void createUserWithEmptyNameTest() {
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
    void updateUserTest() {
        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User expectedUser = userController.createUser(user);
        expectedUser.setLogin(LOGIN_SECOND);
        expectedUser.setEmail(E_MAIL_SECOND);
        expectedUser.setName(NAME_SECOND);
        expectedUser.setBirthday(BIRTHDAY_SECOND);
        User actualUpdated = userController.updateUser(expectedUser);

        assertEquals(expectedUser, actualUpdated);
    }

    @Test
    void updateWrongUserTest() {
        User userWithWrongId = User.builder()
                .id(-1L)
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        final NotFoundException[] notFoundExceptions = new NotFoundException[1];
        assertAll(
                () -> notFoundExceptions[0] = assertThrows(NotFoundException.class, () -> userController.updateUser(userWithWrongId)),
                () -> assertEquals("Пользователь c id: -1 не существует", notFoundExceptions[0].getMessage(), MessageFormat.format("Проверка сообщения об ошибке для кейса: {0}", "id: -1"))
        );
    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    void validateUserTest(TestData testData) {
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