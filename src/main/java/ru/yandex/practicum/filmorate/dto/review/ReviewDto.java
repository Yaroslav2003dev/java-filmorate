package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class ReviewDto {
    private Long userId;
    private Long filmId;
    private String review;
}
