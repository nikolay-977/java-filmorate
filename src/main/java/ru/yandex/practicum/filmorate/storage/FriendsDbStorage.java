package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    private static final String SQL_QUERY_ADD_FRIENDSHIP = "INSERT INTO FRIENDSHIP(user_id, friend_id, status_id) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_FRIENDSHIP = "DELETE FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_QUERY_GET_FRIENDS = "SELECT * FROM USERS u " +
            "JOIN FRIENDSHIP f ON f.friend_id = u.id " +
            "WHERE f.user_id = ?";
    private static final String SQL_QUERY_GET_COMMON_FRIENDS = "SELECT * FROM USERS u " +
            "JOIN FRIENDSHIP f1 ON f1.friend_id = u.id " +
            "JOIN FRIENDSHIP f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id= ?;";

    @Override
    public void addFriends(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_QUERY_ADD_FRIENDSHIP, userId, friendId, 2);
    }

    @Override
    public void removeFriends(Long userId, Long friendId) {
        jdbcTemplate.update(SQL_DELETE_FRIENDSHIP, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return jdbcTemplate.query(SQL_QUERY_GET_FRIENDS, userMapper, userId);
    }

    @Override
    public List<User> getCommonFriends(Long id, Long otherId) {
        return jdbcTemplate.query(SQL_QUERY_GET_COMMON_FRIENDS, userMapper, id, otherId);
    }
}
