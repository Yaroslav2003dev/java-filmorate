package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> implements DirectorStorage {

    private static final String GET_ALL_QUERY = "SELECT * FROM directors";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES(?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Director> findAllDirectors() {
        return findMany(GET_ALL_QUERY);
    }

    @Override
    public Optional<Director> getDirectorById(Long id) {
        return findOne(GET_BY_ID_QUERY, id);
    }

    @Override
    public Director addDirector(Director director) {
        Long id = insert(
                INSERT_QUERY,
                director.getName()
        );
        director.setId(id);
        return director;
    }

    @Override
    public void updateDirector(Director director) {
        update(
                UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
    }

    @Override
    public void deleteDirector(Long id) {
        delete(
                DELETE_QUERY,
                id
        );
    }
}
