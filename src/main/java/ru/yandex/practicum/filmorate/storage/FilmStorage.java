package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Long create(Film film);

    Collection<Film> findAll();

    Film getFilmById(Long id);

    List<Film> getFilmsByDirector(Long directorId, String sortBy);

}
