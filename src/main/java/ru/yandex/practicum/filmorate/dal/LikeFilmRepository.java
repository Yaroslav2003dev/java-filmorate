package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public class LikeFilmRepository extends BaseRepository<Film> implements LikeStorage {
    private static final String SORT_FILM_DESC_LIMIT_QUERY = "SELECT f.id, f.name, mpa_id, f.description, f.release_date, f.duration\n" +
            "FROM like_film lf\n" +
            "JOIN Film f ON lf.film_id = f.id\n" +
            "GROUP BY f.id, f.name, mpa_id, f.description, f.release_date, f.duration\n" +
            "ORDER BY COUNT(lf.user_id) DESC\n" +
            "LIMIT ?";

    private static final String INSERT_QUERY = "INSERT INTO like_film(film_id, user_id)" +
            "VALUES (?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM like_film WHERE film_id = ? AND user_id = ?";

    private static final String SORT_FILM_BY_GENRE_YEAR_DESC_LIMIT_QUERY =
            "SELECT f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "FROM like_film lf " +
                    "JOIN film f ON lf.film_id = f.id " +
                    "JOIN FilmGenre fg ON f.id = fg.film_id " +
                    "WHERE fg.genre_id = ? " +
                    "AND f.release_date BETWEEN ? AND ? " +
                    "GROUP BY f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "ORDER BY COUNT(lf.user_id) DESC " +
                    "LIMIT ?";

    public LikeFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Film> topFilms(int limit) {
        return jdbc.query(SORT_FILM_DESC_LIMIT_QUERY, mapper, limit);
    }

    public Collection<Film> popularFilmByYearAndGenre(int genreId, LocalDate yearStart, LocalDate yearEnd, int count) {
        return jdbc.query(SORT_FILM_BY_GENRE_YEAR_DESC_LIMIT_QUERY, mapper, genreId, yearStart, yearEnd, count);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbc.update(INSERT_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        update(
                DELETE_QUERY,
                filmId,
                userId
        );
    }
}
