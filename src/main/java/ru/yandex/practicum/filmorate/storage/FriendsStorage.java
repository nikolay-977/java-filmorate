package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {
    void addFriends(Long userId, Long friendId);

    void removeFriends(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    List<User> getCommonFriends(Long id, Long otherId);
}
