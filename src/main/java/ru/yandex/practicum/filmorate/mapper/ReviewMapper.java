package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

public class ReviewMapper {

    public static Review mapToReview(NewReviewRequest request) {
        Review review = new Review();
        review.setUserId(request.getUserId());
        review.setFilmId(request.getFilmId());
        review.setReview(request.getReview());

        return review;
    }

    public static ReviewDto mapToDto(Review review) {
        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setUserId(review.getUserId());
        reviewDto.setFilmId(review.getFilmId());
        reviewDto.setReview(review.getReview());

        return reviewDto;
    }
}
