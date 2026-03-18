package ru.yandex.practicum.filmorate.dto.review;

import lombok.Data;

@Data
public class UpdateReviewRequest {
    private Long reviewId;
    private String content;
    private Boolean isPositive;
    private Long userId;

    public boolean hasUserId() {
        return userId != null;
    }

    public boolean hasReviewId() {
        return reviewId != null;
    }

    public boolean hasContent() {
        return content != null && !content.isBlank();
    }

    public boolean hasIsPositive() {
        return isPositive != null;
    }
}
