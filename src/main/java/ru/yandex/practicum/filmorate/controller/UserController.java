package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping()
    public HashMap<Integer, User> getAllUsers() {
        return users;
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        validate(user);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        validate(user);
        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }

    private void validate(User user) {
        validateEmail(user);
        validateLogin(user);
        validateName(user);
        validateBirthday(user);
        log.info("Валидация пользователя пройдена успешно");
    }

    private void validateEmail(User user) {
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации электронной почты пользователя");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
    }

    private void validateLogin(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации логина пользователя");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
    }

    private void validateName(User user) {
        if (user.getName().isEmpty()){
            log.info("Имя пользователя не указано");
            user.setName(user.getLogin());
        }
    }

    private void validateBirthday(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации даты рождения пользователя");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
