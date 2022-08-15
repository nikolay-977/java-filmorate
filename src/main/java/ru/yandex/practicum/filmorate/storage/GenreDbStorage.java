package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreMapper genreMapper;
    private static final String SQL_QUERY_GET_ALL_GENRES = "SELECT * FROM GENRES";

    private static final String SQL_QUERY_GET_GENRE_BY_ID = "SELECT * FROM GENRES " +
            "WHERE ID = ?";

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SQL_QUERY_GET_ALL_GENRES, genreMapper);
    }

    @Override
    public Optional<Genre> getGenre(Long id) {
        List<Genre> genreList = jdbcTemplate.query(SQL_QUERY_GET_GENRE_BY_ID, genreMapper, id);
        if (genreList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(genreList.get(0));
    }
}
