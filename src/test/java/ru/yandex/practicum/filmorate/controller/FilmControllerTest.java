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
import java.util.ArrayList;
import java.util.List;
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
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .build();

        Film filmTwo = Film.builder()
                .name(NAME_SECOND)
                .description(DESCRIPTION_SECOND)
                .releaseDate(RELEASE_DATE_SECOND)
                .duration(DURATION_SECOND)
                .build();

        Film filmOneCreated = filmController.createFilm(filmOne);
        Film filmTwoCreated = filmController.createFilm(filmTwo);

        List<Film> expectedFilms = new ArrayList<>(List.of(filmOneCreated, filmTwoCreated));

        List<Film> actualFilms = filmController.getAllFilms();
        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    void createFilm() {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .build();

        Film filmCreated = filmController.createFilm(film);

        assertEquals(film, filmCreated);
    }

    @Test
    void updateFilm() {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
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
    void updateWrongFilm() {
        FilmController filmController = new FilmController();

        Film film = Film.builder()
                .name(NAME_FIRST)
                .description(DESCRIPTION_FIRST)
                .releaseDate(RELEASE_DATE_FIRST)
                .duration(DURATION_FIRST)
                .build();

        Film filmCreated = filmController.createFilm(film);
        filmCreated.setId(-1);
        filmCreated.setName(NAME_SECOND);
        filmCreated.setDescription(DESCRIPTION_SECOND);
        filmCreated.setReleaseDate(RELEASE_DATE_SECOND);
        filmCreated.setDuration(DURATION_SECOND);

        final ValidationException[] validationException = new ValidationException[1];
        assertAll(
                () -> validationException[0] = assertThrows(ValidationException.class, () -> filmController.updateFilm(filmCreated)),
                () -> assertEquals("?????????? c id: -1 ???? ????????????????????", validationException[0].getMessage(), MessageFormat.format("???????????????? ?????????????????? ???? ???????????? ?????? ??????????: {0}", "id: -1"))
        );

    }

    @ParameterizedTest
    @MethodSource("testDataProvider")
    void validateFilmTest(TestData testData) {
        FilmController filmController = new FilmController();
        final ValidationException[] validationException = new ValidationException[1];
        assertAll(
                () -> validationException[0] = assertThrows(ValidationException.class, () -> filmController.createFilm(testData.getFilm()), testData.checkName),
                () -> assertEquals(testData.expectedMessage, validationException[0].getMessage(), MessageFormat.format("???????????????? ?????????????????? ???? ???????????? ?????? ??????????: {0}", testData.checkName))
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
                                .build())
                        .expectedMessage("???????????????? ???? ?????????? ???????? ????????????")
                        .checkName("???????????? ????????????????")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST + "i")
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(DURATION_FIRST)
                                .build())
                        .expectedMessage("???????????????????????? ?????????? ???????????????? ??? 200 ????????????????")
                        .checkName("201 ????????????")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(LocalDate.of(1895, 12, 27))
                                .duration(DURATION_FIRST)
                                .build())
                        .expectedMessage("???????? ???????????? ???????????? ???????? ???? ???????????? 28 ?????????????? 1895 ????????")
                        .checkName("???????? ???????????? 27 ?????????????? 1895 ????????")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(-1L)
                                .build())
                        .expectedMessage("?????????????????????????????????? ???????????? ???????????? ???????? ??????????????????????????")
                        .checkName("?????????????????????????? ?????????????????????????????????? ????????????")
                        .build(),
                TestData.builder()
                        .film(Film.builder()
                                .name(NAME_FIRST)
                                .description(DESCRIPTION_FIRST)
                                .releaseDate(RELEASE_DATE_FIRST)
                                .duration(0L)
                                .build())
                        .expectedMessage("?????????????????????????????????? ???????????? ???????????? ???????? ??????????????????????????")
                        .checkName("?????????????? ?????????????????????????????????? ????????????")
                        .build(),
                TestData.builder()
                        .film(null)
                        .expectedMessage("?????????? ???? ?????????? ???????? null")
                        .checkName("?????????? is null")
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