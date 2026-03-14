package ru.yandex.practicum.filmorate.storage;

public interface LikeStorage {
    void addLike(Long id, Long userId);

    void deleteLike(Long id, Long userId);
}
