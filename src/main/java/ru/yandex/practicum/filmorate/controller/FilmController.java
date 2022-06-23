package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static Integer nextId = 0;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping()
    public HashMap<Integer, Film> getAllFilms() {
        return films;
    }

    @PostMapping()
    public Film createFilm(@RequestBody Film film) {
        validate(film);
        film.setId(++nextId);
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return films.get(film.getId());
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        if(!films.containsKey(film.getId())){
            log.warn("Ошибка валидации id фильма");
            throw new ValidationException(MessageFormat.format("Фильм c id: {0} не существует", film.getId()));
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return films.get(film.getId());
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
            log.warn("Ошибка валидации фильма");
            throw new ValidationException("Фильм не может быть null");
        }
    }

    private void validateName(Film film) {
        if (film.getName().isEmpty()) {
            log.warn("Ошибка валидации названия фильма");
            throw new ValidationException("Название не может быть пустым");
        }
    }

    private void validateDescription(Film film) {
        if (film.getDescription().length() > 200) {
            log.warn("Ошибка валидации описания фильма");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Ошибка валидации даты релиза фильма");
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }

    private void validateDuration(Film film) {
        if (film.getDuration() <= 0L) {
            log.warn("Ошибка валидации продолжительности фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
