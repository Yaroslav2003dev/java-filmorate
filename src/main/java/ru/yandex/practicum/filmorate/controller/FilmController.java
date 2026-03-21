package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;


import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public ResponseEntity<Collection<FilmDto>> findAll() {
        return new ResponseEntity<>(filmService.findAll(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FilmDto> create(@RequestBody NewFilmRequest newFilmRequest) {
        return new ResponseEntity<>(filmService.create(newFilmRequest), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<FilmDto> update(@RequestBody UpdateFilmRequest updateFilmRequest) {
        return new ResponseEntity<>(filmService.update(updateFilmRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FilmDto> update(@PathVariable Long id) {
        return new ResponseEntity<>(filmService.delete(id), HttpStatus.OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<FilmDto> addLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(filmService.addLike(id, userId), HttpStatus.OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<FilmDto> deleteLike(@PathVariable Long id, @PathVariable Long userId) {
        return new ResponseEntity<>(filmService.deleteLike(id, userId), HttpStatus.OK);
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<FilmDto>> getTopFilms(
            @RequestParam(required = false) Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) Integer year) {

        if (genreId == null && year == null) {
            if (count == null) {
                count = 10;
            }
            return ResponseEntity.ok(filmService.getTopFilms(count));
        } else {
            return ResponseEntity.ok(
                    filmService.getMostPopularsFilmByGenreAndYear(genreId, year));
        }
    }

    @GetMapping("/common")
    public ResponseEntity<Collection<FilmDto>> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        return new ResponseEntity<>(filmService.getCommonFilms(userId, friendId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable Long id) {
        return new ResponseEntity<>(filmService.getFilmById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<FilmDto>> search(@RequestParam("query") String query,
                                                      @RequestParam("by") List<String> by) {
        return new ResponseEntity<>(filmService.search(query, by), HttpStatus.OK);
    }

    @GetMapping("/director/{directorId}")
    public ResponseEntity<Collection<FilmDto>> getFilmsByDirector(@PathVariable Long directorId,
                                                                  @RequestParam String sortBy) {
        return new ResponseEntity<>(filmService.getFilmsByDirector(directorId, sortBy), HttpStatus.OK);
    }

    public FilmDto getFilmByIdInner(Long id) {
        return filmService.getFilmById(id);
    }
}
