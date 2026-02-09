package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.swing.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.Comparator;


@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() != null && film.getDuration() <= 0) {
            log.warn("продолжительность фильма должна быть положительным числом");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        }
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        Film film = filmStorage.getFilmById(newFilm.getId());
        if (newFilm.getName() != null && newFilm.getName().isBlank()) {
            log.warn("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        } else {
            film.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
            log.warn("максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        } else {
            film.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза не может быть раньше 28 декабря 1895 года");
        } else {
            film.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null && newFilm.getDuration() <= 0) {
            log.warn("продолжительность фильма должна быть положительным числом");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        } else {
            film.setDuration(newFilm.getDuration());
        }
        log.info("Обновлена информация о фильме: {}", film);
        return film;
    }

    public Film addLike(Long id, Long userId) {
        filmStorage.getFilmById(id);
        userStorage.getUserById(userId);
        return filmStorage.addLike(id, userId);
    }

    public Film deleteLike(Long id, Long userId) {
        filmStorage.getFilmById(id);
        userStorage.getUserById(userId);
        return filmStorage.deleteLike(id, userId);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Collection<Film> getTopFilms(Integer count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.reverseOrder())
                .limit(count)
                .toList();
    }
}


