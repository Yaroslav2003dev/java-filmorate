package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.get(id) == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не был найден");
        }
        return films.get(id);
    }

    @Override
    public Film addLike(Long id, Long userId) {
        Film film = getFilmById(id);
        if (film.getLikesIdUsers().contains(userId)) {
            return film;
        } else {
            film.getLikesIdUsers().add(userId);
            return film;
        }
    }

    @Override
    public Film deleteLike(Long id, Long userId) {
        Film film = getFilmById(id);
        if (film.getLikesIdUsers().contains(userId)) {
            film.getLikesIdUsers().remove(userId);
            return film;
        } else {
            return film;
        }
    }


    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
