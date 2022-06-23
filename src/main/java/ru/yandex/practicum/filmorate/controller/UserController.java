package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static Integer nextId = 0;
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping()
    public HashMap<Integer, User> getAllUsers() {
        return users;
    }

    @PostMapping()
    public User createUser(@RequestBody User user) {
        validate(user);
        user.setId(++nextId);
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return users.get(user.getId());
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        validate(user);
        if(!users.containsKey(user.getId())){
            log.warn("Ошибка валидации id пользователя");
            throw new ValidationException(MessageFormat.format("Пользователь c id: {0} не существует", user.getId()));
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return users.get(user.getId());
    }

    private void validate(User user) {
        validateNotNull(user);
        validateEmail(user);
        validateLogin(user);
        validateName(user);
        validateBirthday(user);
        log.info("Валидация пользователя пройдена успешно");
    }

    private void validateNotNull(User user) {
        if(user == null){
            log.warn("Ошибка валидации пользователя");
            throw new ValidationException("Пользователь не может быть null");
        }
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
