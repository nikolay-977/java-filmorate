package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAllUsers();

    User createUser(User user);

    Optional<User> getUser(Long id);

    User updateUser(User user);

    void deleteUser(Long id);
}
