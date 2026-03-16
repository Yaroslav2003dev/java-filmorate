package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> implements ReviewStorage {

    private static final String INSERT_REVIEW_QUERY =
            "INSERT INTO review (content, is_positive, user_id, film_id, useful) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_REVIEW_QUERY =
            "UPDATE review SET content = ?, is_positive = ?, user_id = ?, film_id = ? " +
                    "WHERE id = ?";
    private static final String DELETE_REVIEW_QUERY =
            "DELETE FROM review WHERE id = ?";
    private static final String GET_REVIEW_BY_ID_QUERY =
            "SELECT id, content, is_positive, user_id, film_id, useful " +
                    "FROM review " +
                    "WHERE id = ?";
    private static final String GET_ALL_REVIEWS_QUERY =
            "SELECT id, content, is_positive, user_id, film_id, useful " +
                    "FROM review " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
    private static final String GET_REVIEWS_BY_FILM_QUERY =
            "SELECT id, content, is_positive, user_id, film_id, useful " +
                    "FROM review " +
                    "WHERE film_id = ? " +
                    "ORDER BY useful DESC " +
                    "LIMIT ?";
    private static final String INSERT_REACTION_QUERY =
            "INSERT INTO review_reaction (review_id, user_id, is_like) " +
                    "VALUES (?, ?, ?)";
    private static final String GET_REACTION_QUERY =
            "SELECT is_like " +
                    "FROM review_reaction " +
                    "WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL_PLUS_ONE =
            "UPDATE review " +
                    "SET useful = useful + 1 " +
                    "WHERE id = ?";
    private static final String UPDATE_USEFUL_MINUS_ONE =
            "UPDATE review " +
                    "SET useful = useful - 1 " +
                    "WHERE id = ?";
    private static final String UPDATE_REACTION_QUERY =
            "UPDATE review_reaction " +
                    "SET is_like = ? " +
                    "WHERE review_id = ? AND user_id = ?";
    private static final String UPDATE_USEFUL_PLUS_TWO =
            "UPDATE review " +
                    "SET useful = useful + 2 " +
                    "WHERE id = ?";
    private static final String UPDATE_USEFUL_MINUS_TWO =
            "UPDATE review " +
                    "SET useful = useful - 2 " +
                    "WHERE id = ?";
    private static final String DELETE_REACTION_QUERY =
            "DELETE FROM review_reaction " +
                    "WHERE review_id = ? AND user_id = ?";


    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Review addReview(Review review) {
        try {
            long id = insert(
                    INSERT_REVIEW_QUERY,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUserId(),
                    review.getFilmId(),
                    0L
            );
            review.setReviewId(id);
            review.setUseful(0);
            return review;
        } catch (Exception e) {
            System.err.println("Error saving review: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateReview(Review review) {
        update(
                UPDATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId()
        );
    }

    @Override
    public void deleteReview(Long id) {
        delete(DELETE_REVIEW_QUERY, id);
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return findOne(GET_REVIEW_BY_ID_QUERY, id);
    }

    @Override
    public List<Review> findAllReviews(Long filmId, int count) {
        if (filmId == null) {
            return findMany(GET_ALL_REVIEWS_QUERY, count);
        } else {
            return findMany(GET_REVIEWS_BY_FILM_QUERY, filmId, count);
        }
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        Boolean reaction = getReaction(reviewId, userId);
        if (reaction == null) {
            update(INSERT_REACTION_QUERY, reviewId, userId, true);
            update(UPDATE_USEFUL_PLUS_ONE, reviewId);
        } else if (!reaction) {
            update(UPDATE_REACTION_QUERY, true, reviewId, userId);
            update(UPDATE_USEFUL_PLUS_TWO, reviewId);
        }
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        Boolean reaction = getReaction(reviewId, userId);

        if (reaction == null) {
            update(INSERT_REACTION_QUERY, reviewId, userId, false);
            update(UPDATE_USEFUL_MINUS_ONE, reviewId);
        } else if (reaction) {
            update(UPDATE_REACTION_QUERY, false, reviewId, userId);
            update(UPDATE_USEFUL_MINUS_TWO, reviewId);
        }
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        Boolean reaction = getReaction(reviewId, userId);

        if (Boolean.TRUE.equals(reaction)) {
            update(DELETE_REACTION_QUERY, reviewId, userId);
            update(UPDATE_USEFUL_MINUS_ONE, reviewId);
        }
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        Boolean reaction = getReaction(reviewId, userId);
        if (Boolean.FALSE.equals(reaction)) {
            update(DELETE_REACTION_QUERY, reviewId, userId);
            update(UPDATE_USEFUL_PLUS_ONE, reviewId);
        }
    }

    private Boolean getReaction(Long reviewId, Long userId) {
        List<Boolean> result = jdbc.query(
                GET_REACTION_QUERY,
                (rs, rowNum) -> rs.getBoolean("is_like"),
                reviewId,
                userId
        );

        return result.isEmpty() ? null : result.get(0);
    }
}
