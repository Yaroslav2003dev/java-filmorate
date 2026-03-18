package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventType;
import ru.yandex.practicum.filmorate.service.Operation;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.util.Collection;

@Repository
public class EventRepository extends BaseRepository<Event> implements EventStorage {
    private static final String FIND_EVENT_BY_ID_QUERY = "SELECT * FROM Event WHERE id = ?";
    private static final String FIND_EVENTS_BY_USER_ID_QUERY = "SELECT * FROM Event WHERE user_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO event(event_timestamp, user_id, event_type, operation, entity_id)" +
            " VALUES (?, ?, ?, ?, ?)";

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Collection<Event> getEventsByUserId(Long userId) {
        if (findMany(FIND_EVENTS_BY_USER_ID_QUERY, userId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + userId + " не был найден");
        }
        return findMany(FIND_EVENTS_BY_USER_ID_QUERY, userId);
    }

    @Override
    public Long create(Long eventTimestamp, Long userId, EventType event_type, Operation operation, Long entityId) {
        return insert(
                INSERT_QUERY,
                eventTimestamp,
                userId,
                event_type.name(),
                operation.name(),
                entityId
        );
    }

    @Override
    public Event getEventById(Long eventId) {
        if (findOne(FIND_EVENT_BY_ID_QUERY, eventId).isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + eventId + " не был найден");
        }
        return findOne(FIND_EVENT_BY_ID_QUERY, eventId).get();
    }

}
