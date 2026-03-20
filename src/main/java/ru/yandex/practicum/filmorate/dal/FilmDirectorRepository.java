package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Repository
public class FilmDirectorRepository extends BaseRepository<Director> {

    private static final String INSERT_QUERY =
            "INSERT INTO film_directors (film_id, director_id) VALUES (?, ?)";

    private static final String FIND_BY_FILM_ID_QUERY =
            "SELECT d.* FROM directors d " +
                    "JOIN film_directors fd ON d.id = fd.director_id " +
                    "WHERE fd.film_id = ?";

    private static final String DELETE_BY_FILM_ID_QUERY =
            "DELETE FROM film_directors WHERE film_id = ?";

    public FilmDirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> getDirectorsByFilmId(Long filmId) {
        return jdbc.query(FIND_BY_FILM_ID_QUERY, mapper, filmId);
    }

    public void save(Long filmId, Long directorId) {
        jdbc.update(INSERT_QUERY, filmId, directorId);
    }

    public void deleteByFilmId(Long filmId) {
        jdbc.update(DELETE_BY_FILM_ID_QUERY, filmId);
    }
}
