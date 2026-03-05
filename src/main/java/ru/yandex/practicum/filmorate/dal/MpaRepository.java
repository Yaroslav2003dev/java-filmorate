package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Mpa findById(Long mpaId) {
        Optional<Mpa> mpa = findOne(FIND_BY_ID_QUERY, mpaId);
        if (mpa.isEmpty()) {
            throw new NotFoundException("Категория с id = " + mpaId + " не была найдена");
        }
        return mpa.get();
    }
}
