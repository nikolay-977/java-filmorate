package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private static Long nextId = 0L;
    private final static LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film createFilm(Film film) {
        validate(film);
        film.setId(++nextId);
        if(film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return filmStorage.createFilm(film);
    }

    public Film getFilm(Long id) {
        validateFilmExist(id);
        return filmStorage.getFilm(id);
    }

    public List<Film> getPopularFilms(Long count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film updateFilm(Film film) {
        validate(film);
        validateFilmExist(film.getId());
        return filmStorage.updateFilm(film);
    }

    public Film addLike(Long id, Long userId) {
        validateFilmExist(id);
        validateUserExist(userId);
        return filmStorage.addLike(id, userId);
    }

    public Film removeLike(Long id, Long userId) {
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
        if (film.getName().isEmpty()) {
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
}
