package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdateReviewRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto addReview(@Valid @RequestBody NewReviewRequest request) {
        return reviewService.createReview(request);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody UpdateReviewRequest request) {
        return reviewService.updateReview(request);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable("id") Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return reviewService.deleteReview(id);
    }

    @GetMapping("/{id}")
    public ReviewDto getReviewById(@PathVariable("id") Long id) {
        if (id == null || id < 0) {
            throw new ValidationException("Некорректный Id");
        }
        return reviewService.getReviewById(id);
    }

    @GetMapping
    public List<ReviewDto> getReviews(
            @RequestParam(required = false) Long filmId,
            @RequestParam(defaultValue = "10", name = "count") Integer count) {

        if (count <= 0) {
            throw new ValidationException("count должен быть больше нуля");
        }

        return reviewService.findAllReview(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public boolean addDisLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.addDisLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public boolean deleteDisLike(
            @PathVariable Long id,
            @PathVariable Long userId) {
        return reviewService.deleteDisLike(id, userId);
    }

}
