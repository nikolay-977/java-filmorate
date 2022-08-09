package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> getAllGenres() {
        return genreStorage.getAllGenres();
    }

    public Optional<Genre> getGenre(Long id) {
        validateGenreExist(id);
        return genreStorage.getGenre(id);
    }

    private void validateGenreExist(Long id) {
        boolean isContainsId = genreStorage.getAllGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList()).contains(id);
        if (!isContainsId) {
            log.warn(MessageFormat.format("Жанр c id: {0} не существует", id));
            throw new NotFoundException(MessageFormat.format("Жанр c id: {0} не существует", id));
        }
    }
}
