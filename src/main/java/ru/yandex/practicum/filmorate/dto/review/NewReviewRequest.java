package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewReviewRequest {
    @NotEmpty(message = "Отзыв не должен быть пустым")
    private String content;
    private Boolean isPositive;
    @NotNull(message = "Id пользователя должен быть указан")
    private Long userId;
    @NotNull(message = "Id фильма должен быть указан")
    private Long filmId;
}
