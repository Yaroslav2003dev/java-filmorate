package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
public class FilmGenreRepository extends BaseRepository<Genre> {

    static final String INSERT_QUERY = "INSERT INTO FilmGenre (genre_id, film_id) " +
            "VALUES (?,?)";
    private static final String FIND_BY_ID_FILM_QUERY = "SELECT g.* FROM Genre g JOIN FilmGenre fg " +
            "ON g.id=fg.genre_id" +
            " WHERE fg.film_id = ?";
    private static final String DELETE_BY_FILM = "DELETE FROM FilmGenre WHERE film_id = ?";

    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }


    public List<Genre> getListIdFilmGenreById(Long filmId) {
        return jdbc.query(FIND_BY_ID_FILM_QUERY, mapper, filmId);
    }

    public void save(Long genreId, Long filmId) {
        update(INSERT_QUERY, genreId, filmId);
    }

    public void delete(Long filmId) {
        delete(DELETE_BY_FILM, filmId);
    }

}
