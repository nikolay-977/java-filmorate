package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(Film film);

    Optional<Film> getFilm(Long id);

    Film updateFilm(Film film);

    void deleteFilm(Long id);

    List<User> addLike(Long id, Long userId);

    List<User> removeLike(Long id, Long userId);

    List<Film> getPopularFilms(Long count);
}
