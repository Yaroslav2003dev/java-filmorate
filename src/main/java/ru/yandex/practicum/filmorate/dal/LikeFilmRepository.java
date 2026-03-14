package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

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

    public LikeFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Film> topFilms(int limit) {
        return jdbc.query(SORT_FILM_DESC_LIMIT_QUERY, mapper, limit);
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
