package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User getUserById(Long id);

    User addFriend(Long id, Long friendId);

    Collection<User> getAllFriends(Long id);

    User deleteFriend(Long id, Long friendId);
}
