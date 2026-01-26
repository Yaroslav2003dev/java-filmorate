package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
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
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        Film film = films.get(newFilm.getId());
        if (newFilm.getName() != null && newFilm.getName().isBlank()) {
            log.warn("название не может быть пустым");
            throw new ValidationException("название не может быть пустым");
        } else if (newFilm.getName() != null) {
            film.setName(newFilm.getName());
        }

        if (newFilm.getDescription() != null && newFilm.getDescription().length() > 200) {
            log.warn("максимальная длина описания — 200 символов");
            throw new ValidationException("максимальная длина описания — 200 символов");
        } else if (newFilm.getDescription() != null) {
            film.setDescription(newFilm.getDescription());
        }

        if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isBefore(LocalDate.of(1895, Month.DECEMBER, 28))) {
            log.warn("дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("дата релиза не может быть раньше 28 декабря 1895 года");
        } else if (newFilm.getReleaseDate() != null) {
            film.setReleaseDate(newFilm.getReleaseDate());
        }

        if (newFilm.getDuration() != null && newFilm.getDuration() <= 0) {
            log.warn("продолжительность фильма должна быть положительным числом");
            throw new ValidationException("продолжительность фильма должна быть положительным числом");
        } else if (newFilm.getDuration() != null) {
            film.setDuration(newFilm.getDuration());
        }

        log.info("Обновлена информация о фильме: {}", film);
        return film;
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Film getFilmById(Long id) {
        return films.get(id);
    }
}
