package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendRequest;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FriendRequestRowMapper implements RowMapper<FriendRequest> {
    @Override
    public FriendRequest mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(resultSet.getLong("sender_id"));
        friendRequest.setReceiverId(resultSet.getLong("receiver_id"));

        return friendRequest;
    }
}
