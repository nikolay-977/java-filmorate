package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private static Long nextId = 0L;
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        validateUserExist(id);
        return userStorage.getUser(id);
    }

    public User createUser(User user) {
        validate(user);
        user.setId(++nextId);
        user.setFriends(new HashSet<>());
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        validate(user);
        validateUserExist(user.getId());
        return userStorage.updateUser(user);
    }

    public User addFriends(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        return userStorage.addFriends(userId, friendId);
    }

    public User removeFriends(Long userId, Long friendId) {
        validateUserExist(userId);
        validateUserExist(friendId);
        return userStorage.removeFriends(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
        validateUserExist(userId);
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        validateUserExist(id);
        validateUserExist(otherId);
        return userStorage.getCommonFriends(id, otherId);
    }

    private void validate(User user) throws ValidationException {
        validateNotNull(user);
        validateEmail(user);
        validateLogin(user);
        validateName(user);
        validateBirthday(user);
        log.info("Валидация пользователя пройдена успешно");
    }

    private void validateNotNull(User user) {
        if (user == null) {
            log.warn("Пользователь не может быть null");
            throw new ValidationException("Пользователь не может быть null");
        }
    }

    private void validateUserExist(Long id) {
        boolean isContainsId = userStorage.getAllUsers().stream().map(User::getId)
                .collect(Collectors.toList()).contains(id);
        if (!isContainsId) {
            log.warn(MessageFormat.format("Пользователь c id: {0} не существует", id));
            throw new NotFoundException(MessageFormat.format("Пользователь c id: {0} не существует", id));
        }
    }

    private void validateEmail(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.warn("Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private void validateName(User user) {
        if (user.getName().isEmpty()) {
            log.info("Имя пользователя не указано");
            user.setName(user.getLogin());
        }
    }

    private void validateBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
