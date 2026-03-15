package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public ReviewDto createReview(NewReviewRequest request) {
        if (request.getUserId() == null || request.getFilmId() == null || request.getReview().isBlank()) {
            throw new ValidationException("Поля должны быть заполнены");
        }

        userRepository.getUserById(request.getUserId());
        filmRepository.getFilmById(request.getFilmId());

        Review review = ReviewMapper.mapToReview(request);
        reviewRepository.createdReview(review);
        return ReviewMapper.mapToDto(review);
    }

}
