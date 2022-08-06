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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {

    private final FilmController filmController;
    private final UserController userController;
    private static final String NAME_FIRST = RandomStringUtils.randomAlphabetic(5);
    private static final String NAME_SECOND = RandomStringUtils.randomAlphabetic(10);
    private static final String DESCRIPTION_FIRST = RandomStringUtils.randomAlphabetic(200);
    private static final String DESCRIPTION_SECOND = RandomStringUtils.randomAlphabetic(1);
    private static final Long DURATION_FIRST = 3600000L;
    private static final Long DURATION_SECOND = 7200000L;
    private static final LocalDate RELEASE_DATE_FIRST = LocalDate.of(1895, 12, 28);
    private static final LocalDate RELEASE_DATE_SECOND = LocalDate.now();
    private static final String E_MAIL_FIRST = RandomStringUtils.randomAlphabetic(10) + "@" + RandomStringUtils.randomAlphabetic(10) + ".com";
    private static final String LOGIN_FIRST = RandomStringUtils.randomAlphabetic(10);
    private static final LocalDate BIRTHDAY_FIRST = LocalDate.of(1988, 1, 1);

    @AfterEach
    void tearDown() {
        List<Film> films = filmController.getAllFilms();
        for (Film film : films) {
            filmController.deleteFilm(film.getId());
        }
    }

    @Test
    void getAllFilmsTest() {
        Film firstFilm = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Film secondFilm = Film.builder()
                .name(NAME_SECOND)
                .description(DESCRIPTION_SECOND)
                .releaseDate(RELEASE_DATE_SECOND)
                .duration(DURATION_SECOND)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Film firstCreatedFilm = filmController.createFilm(firstFilm);
        Film secondCreatedFilm = filmController.createFilm(secondFilm);

        List<Film> expectedFilms = List.of(firstCreatedFilm, secondCreatedFilm);

        List<Film> actualFilms = filmController.getAllFilms();
        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    void createFilmTest() {
        HashSet<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(1l).build());

        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .genres(genres)
                .build();

        Film filmCreated = filmController.createFilm(film);

        assertEquals(film, filmCreated);
    }

    @Test
    void getFilmTest() {
        HashSet<Genre> genres = new HashSet<>();
        genres.add(Genre.builder().id(1l).build());

        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .genres(genres)
                .build();

        Film expectedFilm = filmController.createFilm(film);
        Film actualFilm = filmController.getFilm(film.getId());

        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    void updateFilmTest() {
        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Film filmCreated = filmController.createFilm(film);
        filmCreated.setName(NAME_SECOND);
        filmCreated.setDescription(DESCRIPTION_SECOND);
        filmCreated.setReleaseDate(RELEASE_DATE_SECOND);
        filmCreated.setDuration(DURATION_SECOND);
        Film filmUpdated = filmController.updateFilm(filmCreated);

        assertEquals(filmCreated, filmUpdated);
    }

    @Test
    void updateWrongFilmTest() {
        Film filmWithWrongId = Film.builder()
                .id(-1L)
                .name(NAME_SECOND)
                .description(DESCRIPTION_SECOND)
                .releaseDate(RELEASE_DATE_SECOND)
                .duration(DURATION_SECOND)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        final NotFoundException[] notFoundExceptions = new NotFoundException[1];
        assertAll(
                () -> notFoundExceptions[0] = assertThrows(NotFoundException.class, () -> filmController.updateFilm(filmWithWrongId)),
                () -> assertEquals("Фильм c id: -1 не существует", notFoundExceptions[0].getMessage(), MessageFormat.format("Проверка сообщения об ошибке для кейса: {0}", "id: -1"))
        );
    }

    @Test
    void addLikeTest() {
        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Film filmCreated = filmController.createFilm(film);

        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userCreated = userController.createUser(user);

        List<User> userList = filmController.addLike(filmCreated.getId(), userCreated.getId());
        assertTrue(userList.contains(userCreated));
    }

    @Test
    void removeLikeTest() {
        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .rate(4L)
                .mpa(Mpa.builder().id(1L).build())
                .build();

        Film filmCreated = filmController.createFilm(film);

        User user = User.builder()
                .email(E_MAIL_FIRST)
                .login(LOGIN_FIRST)
                .name(NAME_FIRST)
                .birthday(BIRTHDAY_FIRST)
                .build();

        User userCreated = userController.createUser(user);

        filmController.addLike(filmCreated.getId(), userCreated.getId());
        List<User> userList = filmController.removeLike(filmCreated.getId(), userCreated.getId());
        assertFalse(userList.contains(userCreated));
    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    void validateFilmTest(TestData testData) {
        final ValidationException[] validationException = new ValidationException[1];
        assertAll(
                () -> validationException[0] = assertThrows(ValidationException.class, () -> filmController.createFilm(testData.getFilm()), testData.checkName),
                () -> assertEquals(testData.expectedMessage, validationException[0].getMessage(), MessageFormat.format("Проверка сообщения об ошибке для кейса: {0}", testData.checkName))
        );
    }

    private static Stream<TestData> testDataProvider() {
        return Stream.of(
                TestData.builder()
                        .film(Film.builder()
                                .name("")
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(DURATION_FIRST)
                                .rate(4L)
                                .mpa(Mpa.builder().id(1L).build())
                                .build())
                        .expectedMessage("Название не может быть пустым")
                        .checkName("Пустое название")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST + "i")
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(DURATION_FIRST)
                                .rate(4L)
                                .mpa(Mpa.builder().id(1L).build())
                                .build())
                        .expectedMessage("Максимальная длина описания — 200 символов")
                        .checkName("201 символ")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(LocalDate.of(1895, 12, 27))
                                .duration(DURATION_FIRST)
                                .rate(4L)
                                .mpa(Mpa.builder().id(1L).build())
                                .build())
                        .expectedMessage("Дата релиза должна быть не раньше 28 декабря 1895 года")
                        .checkName("Дата релиза 27 декабря 1895 года")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(-1L)
                                .rate(4L)
                                .mpa(Mpa.builder().id(1L).build())
                                .build())
                        .expectedMessage("Продолжительность фильма должна быть положительной")
                        .checkName("Отрицательная продолжительность фильма")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(0L)
                                .rate(4L)
                                .mpa(Mpa.builder().id(1L).build())
                                .build())
                        .expectedMessage("Продолжительность фильма должна быть положительной")
                        .checkName("Нулевая продолжительность фильма")
                        .build(),
                TestData.builder()
                        .film(null)
                        .expectedMessage("Фильм не может быть null")
                        .checkName("Фильм is null")
                        .build()
        );
    }

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    private static class TestData {
        String checkName;
        Film film;
        String expectedMessage;
    }
}