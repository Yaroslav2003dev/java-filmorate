package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import java.util.Collection;
import java.util.List;

@Repository
public class LikeFilmRepository extends BaseRepository<Film> implements LikeStorage {
    private static final String SORT_FILM_DESC_LIMIT_QUERY = "SELECT f.id, f.name, mpa_id, f.description, f.release_date, f.duration\n" +
            "FROM like_film lf\n" +
            "RIGHT JOIN Film f ON lf.film_id = f.id\n" +
            "GROUP BY f.id, f.name, mpa_id, f.description, f.release_date, f.duration\n" +
            "ORDER BY COUNT(lf.user_id) DESC\n" +
            "LIMIT ?";

    private static final String INSERT_QUERY = "INSERT INTO like_film(film_id, user_id)" +
            "VALUES (?, ?)";
    private static final String FIND_BY_FILM_AND_USER = "SELECT f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration FROM like_film lf JOIN Film f ON lf.film_id = f.id WHERE film_id=? AND user_id=?";
    private static final String DELETE_QUERY = "DELETE FROM like_film WHERE film_id = ? AND user_id = ?";
    private static final String SORT_FILM_BY_GENRE_YEAR_LIMIT_QUERY =
            "SELECT f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "FROM LIKE_FILM lf " +
                    "JOIN FILM f ON lf.FILM_ID = f.ID " +
                    "JOIN FILMGENRE f2 ON f.ID  = f2.FILM_ID " +
                    "WHERE f2.GENRE_ID = ? AND YEAR(f.RELEASE_DATE ) = ? " +
                    "GROUP BY f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "ORDER BY COUNT(lf.USER_ID ) DESC ";
    private static final String SORT_FILM_BY_GENRE_LIMIT_QUERY =
            "SELECT f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "FROM LIKE_FILM lf " +
                    "JOIN FILM f ON lf.FILM_ID = f.ID " +
                    "JOIN FILMGENRE f2 ON f.ID  = f2.FILM_ID " +
                    "WHERE f2.GENRE_ID = ? " +
                    "GROUP BY f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "ORDER BY COUNT(lf.USER_ID ) DESC ";
    private static final String SORT_FILM_BY_YEAR_LIMIT_QUERY =
            "SELECT f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "FROM LIKE_FILM lf " +
                    "JOIN FILM f ON lf.FILM_ID = f.ID " +
                    "JOIN FILMGENRE f2 ON f.ID  = f2.FILM_ID " +
                    "WHERE YEAR(f.RELEASE_DATE ) = ? " +
                    "GROUP BY f.id, f.name, f.mpa_id, f.description, f.release_date, f.duration " +
                    "ORDER BY COUNT(lf.USER_ID ) DESC ";

    private static final String GET_RECOMMENDATIONS_QUERY = "SELECT film_id FROM like_film " +
            "WHERE user_id = (" +
            "    SELECT other.user_id " +
            "    FROM like_film target " +
            "    JOIN like_film other ON target.film_id = other.film_id AND target.user_id != other.user_id " +
            "    WHERE target.user_id = ? " +
            "    GROUP BY other.user_id " +
            "    ORDER BY COUNT(other.film_id) DESC " +
            "    LIMIT 1" +
            ") " +
            "AND film_id NOT IN (" +
            "    SELECT film_id FROM like_film WHERE user_id = ? " +
            ")";

    public LikeFilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Film> topFilms(int limit) {
        return jdbc.query(SORT_FILM_DESC_LIMIT_QUERY, mapper, limit);
    }

    public List<Film> mostPopularsFilms(Long genreId, Integer year) {
        if (year == null) {
            return jdbc.query(SORT_FILM_BY_GENRE_LIMIT_QUERY, mapper, genreId);
        }
        if (genreId == null) {
            return jdbc.query(SORT_FILM_BY_YEAR_LIMIT_QUERY, mapper, year);
        }

        return jdbc.query(SORT_FILM_BY_GENRE_YEAR_LIMIT_QUERY, mapper, genreId, year);
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        if (jdbc.query(FIND_BY_FILM_AND_USER, mapper, filmId, userId).isEmpty()) {
            jdbc.update(INSERT_QUERY, filmId, userId);
        }
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        update(
                DELETE_QUERY,
                filmId,
                userId
        );
    }

    public List<Long> getRecommendedFilmsIds(Long userId) {
        return jdbc.queryForList(GET_RECOMMENDATIONS_QUERY, Long.class, userId, userId);
    }
}
