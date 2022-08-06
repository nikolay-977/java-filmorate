package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmsGenresMapper;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmsGenresDbStorage implements FilmsGenresStorage {

    private final JdbcTemplate jdbcTemplate;
    private final FilmsGenresMapper filmsGenresMapper;
    private static final String SQL_QUERY_ADD_FILM_GENRE = "INSERT INTO films_genres(film_id, genre_id) VALUES (?, ?)";
    private static final String SQL_QUERY_DELETE_GENRES = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String SQL_QUERY_SELECT_GENRES = "SELECT * FROM films_genres WHERE film_id = ?";

    @Override
    public void addFilmGenre(Long filmId, Long genreId) {
        jdbcTemplate.update(SQL_QUERY_ADD_FILM_GENRE, filmId, genreId);
    }

    @Override
    public void removeFilmGenres(Long filmId) {
        jdbcTemplate.update(SQL_QUERY_DELETE_GENRES, filmId);
    }

    @Override
    public List<FilmGenre> getFilmGenres(Long filmId) {
        return jdbcTemplate.query(SQL_QUERY_SELECT_GENRES, filmsGenresMapper, filmId);
    }

}
