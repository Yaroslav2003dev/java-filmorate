package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private final FilmGenreRepository filmGenreRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private static final String FIND_ALL_QUERY = "SELECT * FROM film";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(name, mpa_id, description, release_date, duration)" +
            "VALUES (?,?,?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, duration = ?, mpa_id=?, release_date = ? WHERE id = ?";
    private static final String FIND_COMMON_FILMS_QUERY =
            "SELECT f.* FROM film f " +
                    "WHERE f.id IN (SELECT l1.film_id FROM like_film l1 " +
                    "JOIN like_film l2 ON l1.film_id = l2.film_id " +
                    "WHERE l1.user_id = ? AND l2.user_id = ?) " +
                    "ORDER BY (SELECT COUNT(*) FROM like_film WHERE film_id = f.id) DESC, f.id";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmGenreRepository filmGenreRepository, GenreRepository genreRepository, MpaRepository mpaRepository) {
        super(jdbc, mapper);
        this.filmGenreRepository = filmGenreRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
    }

    @Override
    public Long create(Film film) {
        mpaRepository.findById(film.getMpa().getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreRepository.findById(genre.getId());
            }
        }
        Long idFilm = insert(
                INSERT_QUERY,
                film.getName(),
                film.getMpa().getId(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration()
        );
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            for (Genre genre : uniqueGenres) {
                filmGenreRepository.save(genre.getId(), idFilm);
            }
        }
        return idFilm;
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public List<Film> getCommonFilms(Long userId, Long friendId) {
        return findMany(FIND_COMMON_FILMS_QUERY, userId, friendId);
    }


    @Override
    public Film getFilmById(Long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        if (film.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не был найден");
        }
        return film.get();
    }

    public void update(Film film) {
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getId()
        );
    }

    public void delete(Long id) {
        update(DELETE_QUERY, id);
    }

}
