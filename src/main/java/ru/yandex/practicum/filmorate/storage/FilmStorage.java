package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Collection<Film> findAll();

    Film getFilmById(Long id);

    Film addLike(Long id, Long userId);

    Film deleteLike(Long id, Long userId);
}
