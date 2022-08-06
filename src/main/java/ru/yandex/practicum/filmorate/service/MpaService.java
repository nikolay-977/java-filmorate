package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    public Optional<Mpa> getMpa(Long id) {
        validateMpaExist(id);
        return mpaStorage.getMpa(id);
    }

    private void validateMpaExist(Long id) {
        boolean isContainsId = mpaStorage.getAllMpa().stream().map(Mpa::getId)
                .collect(Collectors.toList()).contains(id);
        if (!isContainsId) {
            log.warn(MessageFormat.format("Рейтинг c id: {0} не существует", id));
            throw new NotFoundException(MessageFormat.format("Рейтинг c id: {0} не существует", id));
        }
    }
}
