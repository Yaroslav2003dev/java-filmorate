package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.mappers.IdFriendRowMapper;
import ru.yandex.practicum.filmorate.model.FriendRequest;
import ru.yandex.practicum.filmorate.storage.FriendStorage;

import java.util.List;

@Repository
public class FriendRequestRepository extends BaseRepository<FriendRequest> implements FriendStorage {
    private final UserRepository userRepository;
    private final IdFriendRowMapper idFriendRowMapper;
    private static final String FIND_ALL_FRIENDS_QUERY =
            "SELECT receiver_id AS friend_id " +
                    "FROM friend_request " +
                    "WHERE sender_id = ?";


    private static final String FIND_COMMON_FRIENDS_BY_ID_QUERY =
            "SELECT f1.friend_id " +
                    "FROM ( " +
                    "    SELECT receiver_id AS friend_id " +
                    "    FROM friend_request " +
                    "    WHERE sender_id = ? " +
                    ") f1 " +
                    "JOIN ( " +
                    "    SELECT receiver_id AS friend_id " +
                    "    FROM friend_request " +
                    "    WHERE sender_id = ? " +
                    ") f2 ON f1.friend_id = f2.friend_id";

    private static final String INSERT_QUERY = "INSERT INTO friend_request (sender_id, receiver_id) VALUES (?, ?)";

    private static final String DELETE_QUERY = "DELETE FROM friend_request WHERE sender_id = ? AND receiver_id = ?";

    public FriendRequestRepository(JdbcTemplate jdbc, RowMapper<FriendRequest> mapper, UserRepository userRepository, IdFriendRowMapper idFriendRowMapper) {
        super(jdbc, mapper);
        this.userRepository = userRepository;
        this.idFriendRowMapper = idFriendRowMapper;
    }

    public List<Long> findAllIdFriends(Long userId) {
        return jdbc.query(FIND_ALL_FRIENDS_QUERY, idFriendRowMapper, userId);
    }

    public void deleteFriend(Long senderId, Long receiverId) {
        update(DELETE_QUERY, senderId, receiverId);
    }

    public List<Long> findIdCommonFriendsById(Long senderId, Long receiverId) {
        return jdbc.query(FIND_COMMON_FRIENDS_BY_ID_QUERY, idFriendRowMapper, senderId, receiverId);
    }

    @Transactional
    public void addFriend(Long senderId, Long receiverId) {
        userRepository.getUserById(senderId);
        userRepository.getUserById(receiverId);
        jdbc.update(INSERT_QUERY, senderId, receiverId);
    }

}
