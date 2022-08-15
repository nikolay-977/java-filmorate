package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final FilmsGenresStorage filmsGenresStorage;
    private final GenreStorage genreStorage;

    public List<Film> getAllFilms() {
        List<Film> filmListFromDb = filmStorage.getAllFilms();
        List<Film> filmList = new ArrayList<>();
        if (!filmListFromDb.isEmpty()) {
            for (Film film : filmListFromDb) {
                filmList.add(fillMpa(fillGenres(film)));
            }
        }
        return filmList;
    }

    public Film createFilm(Film film) {
        validate(film);
        return fillMpa(fillGenres(addFilmGenres(filmStorage.createFilm(film))));
    }

    public Film getFilm(Long id) {
        validateFilmExist(id);
        return fillMpa(fillGenres(filmStorage.getFilm(id).get()));
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film updateFilm(Film film) {
        validate(film);
        validateFilmExist(film.getId());
        return fillMpa(fillGenres(updateGenre(filmStorage.updateFilm(film))));
    }

    public void deleteFilm(Long id) {
        validateFilmExist(id);
        filmStorage.deleteFilm(id);
    }

    public List<User> addLike(Long id, Long userId) {
        validateFilmExist(id);
        validateUserExist(userId);
        return filmStorage.addLike(id, userId);
    }

    public List<User> removeLike(Long id, Long userId) {
        validateFilmExist(id);
        validateUserExist(userId);
        return filmStorage.removeLike(id, userId);
    }

    private void validate(Film film) {
        validateNotNull(film);
        validateName(film);
        validateDescription(film);
        validateReleaseDate(film);
        validateDuration(film);
    }

    private void validateNotNull(Film film) {
        if (film == null) {
            log.warn("Фильм не может быть null");
            throw new ValidationException("Фильм не может быть null");
        }
    }

    private void validateFilmExist(Long id) {
        boolean isContainsId = filmStorage.getAllFilms().stream().map(Film::getId)
                .collect(Collectors.toList()).contains(id);
        if (!isContainsId) {
            log.warn(MessageFormat.format("Фильм c id: {0} не существует", id));
            throw new NotFoundException(MessageFormat.format("Фильм c id: {0} не существует", id));
        }
    }

    private void validateUserExist(Long userId) {
        boolean isContainsId = userStorage.getAllUsers().stream().map(User::getId)
                .collect(Collectors.toList()).contains(userId);
        if (!isContainsId) {
            log.warn(MessageFormat.format("Пользователь c id: {0} не существует", userId));
            throw new NotFoundException(MessageFormat.format("Пользователь c id: {0} не существует", userId));
        }
    }

    private void validateName(Film film) {
        if (film.getName() == null | film.getName().isEmpty()) {
            log.warn("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
    }

    private void validateDescription(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            log.warn("Дата релиза должна быть не раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private void validateDuration(Film film) {
        if (film.getDuration() <= 0L) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private Film fillMpa(Film film) {
        if (film.getMpa() != null) {
            Mpa mpa = mpaStorage.getMpa(film.getMpa().getId()).get();
            film.setMpa(mpa);
        }
        return film;
    }

    private Film fillGenres(Film film) {
        List<FilmGenre> filmGenres = filmsGenresStorage.getFilmGenres(film.getId());

        if (!filmGenres.isEmpty()) {
            HashSet<Genre> genres = new HashSet<>();
            for (FilmGenre filmGenre : filmGenres) {
                Genre genre = genreStorage.getGenre(filmGenre.getGenreId()).get();
                genres.add(Genre.builder().id(genre.getId()).name(genre.getName()).build());
            }
            film.setGenres(genres);
        }
        return film;
    }

    private Film addFilmGenres(Film film) {
        HashSet<Genre> genres = film.getGenres();
        if (genres != null) {
            for (Genre genre : genres) {
                filmsGenresStorage.addFilmGenre(film.getId(), genre.getId());
            }
        }
        return film;
    }

    private Film updateGenre(Film film) {
        HashSet<Genre> genres = film.getGenres();
        if (genres != null) {
            filmsGenresStorage.removeFilmGenres(film.getId());
            for (Genre genre : genres) {
                filmsGenresStorage.addFilmGenre(film.getId(), genre.getId());
            }
        }
        return film;
    }
}
