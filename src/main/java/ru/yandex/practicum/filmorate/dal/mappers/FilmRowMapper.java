package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmDirectorRepository;
import ru.yandex.practicum.filmorate.dal.FilmGenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    private final MpaRepository mpaRepository;
    private final FilmGenreRepository filmGenreRepository;
    private final FilmDirectorRepository filmDirectorRepository;

    public FilmRowMapper(MpaRepository mpaRepository, FilmGenreRepository filmGenreRepository, FilmDirectorRepository filmDirectorRepository) {
        this.mpaRepository = mpaRepository;
        this.filmGenreRepository = filmGenreRepository;
        this.filmDirectorRepository = filmDirectorRepository;
    }

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDuration(resultSet.getInt("duration"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getObject("release_date", LocalDate.class));
        Mpa mpa = mpaRepository.findById(resultSet.getLong("mpa_id"));
        film.setMpa(mpa);
        List<Genre> idGenres = filmGenreRepository.getListIdFilmGenreById(resultSet.getLong("id"));
        film.setGenres(idGenres);
        List<Director> directors = filmDirectorRepository.getDirectorsByFilmId(resultSet.getLong("id"));
        film.setDirectors(directors);
        return film;
    }
}
