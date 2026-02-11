package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> findAll() {
        return new ResponseEntity<>(filmService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Film> create(@RequestBody Film film) {
        return new ResponseEntity<>(filmService.create(film), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> update(@RequestBody Film newFilm) {
        return new ResponseEntity<>(filmService.update(newFilm), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(filmService.addLike(id, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Film> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(filmService.deleteLike(id, userId), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getTopFilms(@RequestParam(defaultValue = "10") Integer count) {
        return new ResponseEntity<>(filmService.getTopFilms(count), HttpStatus.OK);
    }

    public Film getFilmById(Long id) {
        return filmService.getFilmById(id);
    }
}
