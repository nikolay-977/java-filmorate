package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;
    private final MpaMapper mpaMapper;
    private static final String SQL_QUERY_GET_ALL_RATES = "SELECT * FROM MPA";
    private static final String SQL_QUERY_GET_RATE_BY_ID = "SELECT * FROM MPA " +
            "WHERE ID = ?";

    @Override
    public List<Mpa> getAllMpa() {
        return jdbcTemplate.query(SQL_QUERY_GET_ALL_RATES, mpaMapper);
    }

    @Override
    public Optional<Mpa> getMpa(Long id) {
        List<Mpa> rateList = jdbcTemplate.query(SQL_QUERY_GET_RATE_BY_ID, mpaMapper, id);
        if (rateList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(rateList.get(0));
    }
}
