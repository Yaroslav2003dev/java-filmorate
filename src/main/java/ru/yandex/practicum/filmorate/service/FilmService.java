package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final LikeFilmRepository likeFilmRepository;
    private final EventService eventService;
    private final DirectorRepository directorRepository;


    @Autowired
    public FilmService(FilmRepository filmRepository, UserRepository userRepository, LikeFilmRepository likeFilmRepository, EventService eventService, DirectorRepository directorRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.likeFilmRepository = likeFilmRepository;
        this.eventService = eventService;
        this.directorRepository = directorRepository;
    }

    public Collection<FilmDto> findAll() {
        return filmRepository.findAll()
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public FilmDto create(NewFilmRequest newFilmRequest) {
        validateCreate(newFilmRequest);
        Film film = FilmMapper.mapToFilm(newFilmRequest);
        Long id = filmRepository.create(film);
        log.info("Создан фильм c id = {}", id);
        return FilmMapper.mapToFilmDto(filmRepository.getFilmById(id));
    }

    public FilmDto update(UpdateFilmRequest newFilm) {
        Film film = filmRepository.getFilmById(newFilm.getId());
        validateUpdate(film, newFilm);
        Film updatedFilm = FilmMapper.updateFilmFields(film, newFilm);
        filmRepository.update(updatedFilm);
        log.info("Обновлена информация о фильме c id = {}", film.getId());
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public FilmDto delete(Long id) {
        Film film = filmRepository.getFilmById(id);
        filmRepository.delete(id);
        log.info("Удалён фильм c id = {}", id);
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto addLike(Long filmId, Long userId) {
        filmRepository.getFilmById(filmId);
        userRepository.getUserById(userId);
        likeFilmRepository.addLike(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, filmId);
        log.info("Добавлен лайк фильму c id = {} пользователем c id = {}", filmId, userId);
        return FilmMapper.mapToFilmDto(filmRepository.getFilmById(filmId));
    }

    public FilmDto deleteLike(Long filmId, Long userId) {
        filmRepository.getFilmById(filmId);
        userRepository.getUserById(userId);
        likeFilmRepository.deleteLike(filmId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, filmId);
        log.info("Удалён лайк фильму c id = {} пользователем c id = {}", filmId, userId);
        return FilmMapper.mapToFilmDto(filmRepository.getFilmById(filmId));
    }

    public FilmDto getFilmById(Long filmId) {
        return FilmMapper.mapToFilmDto(filmRepository.getFilmById(filmId));
    }

    public Collection<FilmDto> getTopFilms(Integer count) {
        return likeFilmRepository.topFilms(count)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    public Collection<FilmDto> search(String query, List<String> by) {
        log.info("Поиск фильмов по запросу:  {} (критерии: {}", query, by);
        List<Film> films = filmRepository.search(query, by);
        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }


    public List<FilmDto> getMostPopularsFilmByGenreAndYear(Long genreId, Integer year) {
        return likeFilmRepository.mostPopularsFilms(genreId, year)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    public Collection<FilmDto> getCommonFilms(Long userId, Long friendId) {

        return filmRepository.getCommonFilms(userId, friendId)
                .stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());

    }

    public Collection<FilmDto> getFilmsByDirector(Long directorId, String sortBy) {

        directorRepository.getDirectorById(directorId);


        if (sortBy == null || (!sortBy.equalsIgnoreCase("year") && !sortBy.equalsIgnoreCase("likes"))) {
            throw new ValidationException("Неверный параметр сортировки. Допустимые значения: year, likes");
        }

        List<Film> films = filmRepository.getFilmsByDirector(directorId, sortBy);
        log.info("Получено {} фильмов режиссера с id = {}, сортировка по {}", films.size(), directorId, sortBy);

        return films.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private void validateUpdate(Film film, UpdateFilmRequest newFilm) {
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
        if (newFilm.getDirectors() != null && !newFilm.getDirectors().isEmpty()) {
            for (Director director : newFilm.getDirectors()) {
                directorRepository.getDirectorById(director.getId());
            }
        }
    }

    private void validateCreate(NewFilmRequest film) {
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
    }
}


