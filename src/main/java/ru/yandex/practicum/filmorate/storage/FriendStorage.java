package ru.yandex.practicum.filmorate.storage;

import java.sql.SQLException;
import java.util.List;

public interface FriendStorage {
    void addFriend(Long id, Long friendId) throws SQLException;

    List<Long> findAllIdFriends(Long id);

    void deleteFriend(Long id, Long friendId);
}
