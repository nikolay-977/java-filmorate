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
import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;
    public static final String SQL_QUERY_GET_ALL_USERS = "SELECT * FROM USERS";
    public static final String SQL_QUERY_CREATE_USER = "INSERT INTO USERS(email, login, name, birthday) " +
            "values (?, ?, ?, ?)";
    public static final String SQL_QUERY_GET_USER_BY_ID = "SELECT * FROM USERS WHERE ID = ?";public static final String SQL_QUERY_UPDATE_USER = "UPDATE USERS " +
            "SET email = ?, login = ?, name = ?, birthday = ? WHERE ID = ?";
    public static final String SQL_DELETE_USER_BY_ID = "DELETE FROM USERS WHERE ID = ?";
    private static final String SQL_QUERY_ADD_FRIENDSHIP = "INSERT INTO FRIENDSHIP(user_id, friend_id, status_id) VALUES (?, ?, ?)";
    private static final String SQL_DELETE_FRIENDSHIP = "DELETE FROM FRIENDSHIP WHERE user_id = ? AND friend_id = ?";
    private static final String SQL_QUERY_GET_FRIENDS = "SELECT * FROM USERS u " +
            "JOIN FRIENDSHIP f ON f.friend_id = u.id " +
            "WHERE f.user_id = ?";
    private static final String SQL_QUERY_GET_COMMON_FRIENDS = "SELECT * FROM USERS u " +
            "JOIN FRIENDSHIP f1 ON f1.friend_id = u.id " +
            "JOIN FRIENDSHIP f2 ON f1.friend_id = f2.friend_id " +
            "WHERE f1.user_id = ? AND f2.user_id= ?;";

    public List<User> getAllUsers() {
        return jdbcTemplate.query(SQL_QUERY_GET_ALL_USERS, userMapper);
    }

    public User createUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_QUERY_CREATE_USER, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setObject(4, user.getBirthday());
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Пользователь добавлен");
        return user;
    }

    @Override
    public Optional<User> getUser(Long id) {
        List<User> userList = jdbcTemplate.query(SQL_QUERY_GET_USER_BY_ID, userMapper, id);
        if (userList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(userList.get(0));
    }

    public User updateUser(User user) {
        jdbcTemplate.update(SQL_QUERY_UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь обновлен");
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update(SQL_DELETE_USER_BY_ID, id);
        log.info("Пользователь удален");
    }

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
