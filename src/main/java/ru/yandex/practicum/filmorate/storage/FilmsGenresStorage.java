package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmsGenresStorage {
    void addFilmGenre(Long filmId, Long userId);

    void removeFilmGenres(Long filmId);

    List<FilmGenre> getFilmGenres(Long filmId);
}
