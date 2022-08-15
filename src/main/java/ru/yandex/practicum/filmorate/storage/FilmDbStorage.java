package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmMapper filmMapper;

    private final UserMapper userMapper;
    private static final String SQL_QUERY_GET_ALL_FILMS = "SELECT * FROM FILMS";
    private static final String SQL_QUERY_CREATE_FILM = "INSERT INTO FILMS(name, description, release_date, duration, rate, mpa_id) " +
            "values (?, ?, ?, ?, ?, ?)";
    private static final String SQL_QUERY_GET_FILM_BY_ID = "SELECT * FROM FILMS " +
            "WHERE ID = ?";
    private static final String SQL_QUERY_UPDATE_FILM = "UPDATE FILMS " +
            "SET name = ?, description = ?, release_date = ?, duration = ? ,rate = ?, mpa_id = ? " +
            "WHERE ID = ?";

    private static final String SQL_DELETE_FILM_BY_ID = "DELETE FROM FILMS WHERE ID = ?";
    private static final String SQL_QUERY_ADD_LIKE = "INSERT INTO LIKES(film_id, user_id) VALUES (?, ?)";
    private static final String SQL_QUERY_DELETE_LIKE = "DELETE FROM LIKES WHERE film_id = ? AND user_id = ?";


    private static final String SQL_QUERY_GET_LIKES = "SELECT * FROM USERS u " +
            "JOIN LIKES l ON l.user_id = u.id " +
            "WHERE film_id = ?";
    private static final String SQL_QUERY_GET_POPULAR_FILMS = "SELECT * FROM FILMS f " +
            "LEFT JOIN LIKES l ON f.id = l.film_id " +
            "GROUP BY f.id " +
            "ORDER BY count(l.user_id) DESC " +
            "LIMIT ?";


    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(SQL_QUERY_GET_ALL_FILMS, filmMapper);
    }

    @Override
    public Film createFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_QUERY_CREATE_FILM, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setObject(3, film.getReleaseDate());
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getRate());
            stmt.setLong(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        log.info("Фильм добавлен");
        return film;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        List<Film> filmList = jdbcTemplate.query(SQL_QUERY_GET_FILM_BY_ID, filmMapper, id);
        if (filmList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(filmList.get(0));
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(SQL_QUERY_UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRate(),
                film.getMpa().getId(),
                film.getId());
        log.info("Фильм обновлен");
        return film;
    }

    @Override
    public void deleteFilm(Long id) {
        jdbcTemplate.update(SQL_DELETE_FILM_BY_ID, id);
        log.info("Фильм удален");
    }

    @Override
    public List<User> addLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_QUERY_ADD_LIKE, filmId, userId);
        return getLikes(filmId);
    }

    @Override
    public List<User> removeLike(Long filmId, Long userId) {
        jdbcTemplate.update(SQL_QUERY_DELETE_LIKE, filmId, userId);
        return getLikes(filmId);
    }

    private List<User> getLikes(Long filmId) {
        return jdbcTemplate.query(SQL_QUERY_GET_LIKES, userMapper, filmId);
    }

    @Override
    public List<Film> getPopularFilms(Long count) {
        return jdbcTemplate.query(SQL_QUERY_GET_POPULAR_FILMS, filmMapper, count);
    }
}
