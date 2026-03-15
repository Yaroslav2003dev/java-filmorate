package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class NewReviewRequest {
    private Long userId;
    private Long filmId;
    private String review;
}
