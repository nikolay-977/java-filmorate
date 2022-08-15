package ru.yandex.practicum.filmorate.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmsGenresMapper implements RowMapper<FilmGenre> {


    @Override
    public FilmGenre mapRow(ResultSet rs, int rowNum) throws SQLException {
        return FilmGenre.builder()
                .filmId(rs.getLong("film_id"))
                .genreId(rs.getLong("genre_id"))
                .build();
    }
}
