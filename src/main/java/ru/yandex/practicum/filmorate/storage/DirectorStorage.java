package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAllDirectors();
    Optional<Director> getDirectorById(Long id);
    Director addDirector(Director director);
    void updateDirector(Director director);
    void deleteDirector(Long id);

}
