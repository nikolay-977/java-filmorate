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
import ru.yandex.practicum.filmorate.model.Film;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static final String NAME_FIRST = RandomStringUtils.randomAlphabetic(1);
    private static final String NAME_SECOND = RandomStringUtils.randomAlphabetic(10);
    private static final String DESCRIPTION_FIRST = RandomStringUtils.randomAlphabetic(200);
    private static final String DESCRIPTION_SECOND = RandomStringUtils.randomAlphabetic(1);
    private static final Long DURATION_FIRST = 3600000L;
    private static final Long DURATION_SECOND = 7200000L;
    private static final LocalDate RELEASE_DATE_FIRST = LocalDate.of(1895, 12, 28);
    private static final LocalDate RELEASE_DATE_SECOND = LocalDate.now();

    @Test
    void getAllFilms() {
        FilmController filmController = new FilmController();

        Film filmOne = Film.builder()
                .id(1)
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .build();

        Film filmTwo = Film.builder()
                .id(2)
                .name(NAME_SECOND)
                .description(DESCRIPTION_SECOND)
                .releaseDate(RELEASE_DATE_SECOND)
                .duration(DURATION_SECOND)
                .build();

        HashMap<Integer, Film> expectedFilms = new HashMap<>();
        expectedFilms.put(filmOne.getId(), filmOne);
        expectedFilms.put(filmTwo.getId(), filmTwo);

        filmController.createFilm(filmOne);
        filmController.createFilm(filmTwo);

        HashMap<Integer, Film> actualFilms = filmController.getAllFilms();
        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    void createFilm() {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .id(1)
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .build();

        Film filmCreated =  filmController.createFilm(film);

        assertEquals(film, filmCreated);
    }

    @Test
    void updateFilm() {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .id(1)
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .build();

        Film filmCreated = filmController.createFilm(film);
        filmCreated.setName(NAME_FIRST + "_updated");
        Film filmUpdated = filmController.updateFilm(filmCreated);

        assertEquals(filmCreated, filmUpdated);
    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    void validateFilmTest(TestData testData) {
        FilmController filmController = new FilmController();
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
                                .id(1)
                                .name("")
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(DURATION_FIRST)
                                .build())
                        .expectedMessage("Название не может быть пустым")
                        .checkName("Пустое название")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .id(1)
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST + "i")
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(DURATION_FIRST)
                                .build())
                        .expectedMessage("Максимальная длина описания — 200 символов")
                        .checkName("201 символ")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .id(1)
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(LocalDate.of(1895, 12, 27))
                                .duration(DURATION_FIRST)
                                .build())
                        .expectedMessage("Дата релиза должна быть не раньше 28 декабря 1895 года")
                        .checkName("Дата релиза 27 декабря 1895 года")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .id(1)
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(-1L)
                                .build())
                        .expectedMessage("Продолжительность фильма должна быть положительной")
                        .checkName("Отрицательная продолжительность фильма")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .id(1)
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(0L)
                                .build())
                        .expectedMessage("Продолжительность фильма должна быть положительной")
                        .checkName("Нулевая продолжительность фильма")
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