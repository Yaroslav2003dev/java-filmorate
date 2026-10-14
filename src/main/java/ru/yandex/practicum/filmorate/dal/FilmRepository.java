package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private final FilmGenreRepository filmGenreRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;
    private final FilmDirectorRepository filmDirectorRepository;
    private static final String FIND_ALL_QUERY = "SELECT * FROM film";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO film(name, mpa_id, description, release_date, duration)" +
            "VALUES (?,?,?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE film SET name = ?, description = ?, duration = ?, mpa_id=?, release_date = ? WHERE id = ?";
    private static final String SEARCH_ORDER_BY = " GROUP BY f.id ORDER BY COUNT(l.user_id) DESC";
    private static final String FIND_COMMON_FILMS_QUERY =
            "SELECT f.* FROM film f " +
                    "WHERE f.id IN (SELECT l1.film_id FROM like_film l1 " +
                    "JOIN like_film l2 ON l1.film_id = l2.film_id " +
                    "WHERE l1.user_id = ? AND l2.user_id = ?) " +
                    "ORDER BY (SELECT COUNT(*) FROM like_film WHERE film_id = f.id) DESC, f.id";
    private static final String DELETE_QUERY = "DELETE FROM film WHERE id = ?";

    private static final String GET_FILMS_BY_DIRECTOR_SORTED_BY_YEAR =
            "SELECT f.* FROM Film f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "WHERE fd.director_id = ? " +
                    "ORDER BY f.release_date";

    private static final String GET_FILMS_BY_DIRECTOR_SORTED_BY_LIKES =
            "SELECT f.* FROM Film f " +
                    "JOIN film_directors fd ON f.id = fd.film_id " +
                    "LEFT JOIN like_film l ON f.id = l.film_id " +
                    "WHERE fd.director_id = ? " +
                    "GROUP BY f.id " +
                    "ORDER BY COUNT(l.user_id) DESC";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmGenreRepository filmGenreRepository, GenreRepository genreRepository, MpaRepository mpaRepository, FilmDirectorRepository filmDirectorRepository) {
        super(jdbc, mapper);
        this.filmGenreRepository = filmGenreRepository;
        this.genreRepository = genreRepository;
        this.mpaRepository = mpaRepository;
        this.filmDirectorRepository = filmDirectorRepository;
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
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            Set<Director> uniqueDirectors = new HashSet<>(film.getDirectors());
            for (Director director : uniqueDirectors) {
                filmDirectorRepository.save(idFilm, director.getId());
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
    public List<Film> getFilmsByDirector(Long directorId, String sortBy) {
        if ("year".equals(sortBy)) {
            return findMany(GET_FILMS_BY_DIRECTOR_SORTED_BY_YEAR, directorId);
        } else if ("likes".equals(sortBy)) {
            return findMany(GET_FILMS_BY_DIRECTOR_SORTED_BY_LIKES, directorId);
        } else {
            throw new ValidationException("Неверный параметр сортировки: " + sortBy);
        }
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
        filmGenreRepository.delete(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                genreRepository.findById(genre.getId());
            }
            Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());
            for (Genre genre : uniqueGenres) {
                filmGenreRepository.save(genre.getId(), film.getId());
            }
        }
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getId()
        );
        filmDirectorRepository.deleteByFilmId(film.getId());

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            Set<Director> uniqueDirectors = new HashSet<>(film.getDirectors());
            for (Director director : uniqueDirectors) {
                filmDirectorRepository.save(film.getId(), director.getId());
            }
        }
    }

    public List<Film> search(String query, List<String> by) {
        String searchPattern = "%" + query.toLowerCase() + "%";

        StringBuilder sql = new StringBuilder(
                "SELECT f.* FROM film f " +
                        "LEFT JOIN like_film l ON f.id = l.film_id "
        );

        List<Object> params = new ArrayList<>();

        if (by.contains("director")) {
            sql.append("LEFT JOIN film_directors fd ON f.id = fd.film_id ");
            sql.append("LEFT JOIN directors d ON fd.director_id = d.id ");
        }

        sql.append("WHERE ");

        if (by.contains("title") && by.contains("director")) {
            sql.append("(LOWER(f.name) LIKE ? OR LOWER(d.name) LIKE ?) ");
            params.add(searchPattern);
            params.add(searchPattern);
        } else if (by.contains("director")) {
            sql.append("LOWER(d.name) LIKE ? ");
            params.add(searchPattern);
        } else {
            sql.append("LOWER(f.name) LIKE ? ");
            params.add(searchPattern);
        }

        sql.append(SEARCH_ORDER_BY);

        return findMany(sql.toString(), params.toArray());
    }

    public void delete(Long id) {
        update(DELETE_QUERY, id);
    }
}
