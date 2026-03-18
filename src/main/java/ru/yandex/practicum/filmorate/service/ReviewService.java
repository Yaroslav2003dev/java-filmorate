package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final EventService eventService;
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewDto createReview(NewReviewRequest request) {
        userStorage.getUserById(request.getUserId());
        filmStorage.getFilmById(request.getFilmId());
        Review review = ReviewMapper.mapToReview(request);
        review = reviewStorage.addReview(review);
        eventService.createEvent(request.getUserId(), EventType.REVIEW, Operation.ADD, review.getReviewId());
        return ReviewMapper.toDto(review);
    }

    public ReviewDto getReviewById(Long id) {
        Review review = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв с id " + id + " не найден"));
        return ReviewMapper.toDto(review);
    }

    public ReviewDto updateReview(UpdateReviewRequest request) {
        Review updateReview = reviewStorage.getReviewById(request.getReviewId())
                .map(review -> ReviewMapper.updateReviewFields(review, request))
                .orElseThrow(() -> new NotFoundException("Отзыв с Id " + request.getReviewId() + " не найден"));
        reviewStorage.updateReview(updateReview);
        eventService.createEvent(request.getUserId(), EventType.REVIEW, Operation.UPDATE, request.getReviewId());
        return ReviewMapper.toDto(updateReview);
    }

    public boolean deleteReview(Long id) {
        Long userId = reviewStorage.getReviewById(id).get().getUserId();
        reviewStorage.getReviewById(id).orElseThrow(
                () -> new NotFoundException("Отзыв с id " + id + " не найден")
        );
        reviewStorage.deleteReview(id);
        eventService.createEvent(userId, EventType.REVIEW, Operation.REMOVE, id);
        return true;
    }

    public List<ReviewDto> findAllReview(Long filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId);
        }
        return reviewStorage.findAllReviews(filmId, count)
                .stream()
                .map(ReviewMapper::toDto)
                .collect(Collectors.toList());
    }

    public boolean addLike(Long reviewId, Long userId) {
        reviewStorage.getReviewById(reviewId).orElseThrow(
                () -> new NotFoundException("Отзыв с id " + reviewId + " не найден"));
        userStorage.getUserById(userId);
        reviewStorage.addLike(reviewId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, reviewId);
        return true;
    }

    public boolean addDisLike(Long reviewId, Long userId) {
        reviewStorage.getReviewById(reviewId).orElseThrow(
                () -> new NotFoundException("Отзыв с id " + reviewId + " не найден"));
        userStorage.getUserById(userId);
        reviewStorage.addDislike(reviewId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.ADD, reviewId);
        return true;
    }

    public boolean deleteLike(Long reviewId, Long userId) {
        reviewStorage.getReviewById(reviewId).orElseThrow(
                () -> new NotFoundException("Отзыв с id " + reviewId + " не найден"));
        userStorage.getUserById(userId);
        reviewStorage.removeLike(reviewId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, reviewId);
        return true;
    }

    public boolean deleteDisLike(Long reviewId, Long userId) {
        reviewStorage.getReviewById(reviewId).orElseThrow(
                () -> new NotFoundException("Отзыв с id " + reviewId + " не найден"));
        userStorage.getUserById(userId);
        reviewStorage.removeDislike(reviewId, userId);
        eventService.createEvent(userId, EventType.LIKE, Operation.REMOVE, reviewId);
        return true;
    }
}
