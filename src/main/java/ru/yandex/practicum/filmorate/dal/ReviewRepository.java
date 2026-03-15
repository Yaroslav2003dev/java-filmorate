package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {

    private static final String INSERT_QUERY = "INSERT INTO film_reviews (user_id, film_id, review) VALUES (?, ?, ?)";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review createdReview(Review review) {
        int rowsAffected = jdbc.update(INSERT_QUERY, review.getUserId(), review.getFilmId(), review.getReview());

        if (rowsAffected == 0) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
        return review;
    }
}
