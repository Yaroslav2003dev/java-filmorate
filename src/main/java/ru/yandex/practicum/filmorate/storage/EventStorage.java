package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.service.EventType;
import ru.yandex.practicum.filmorate.service.Operation;

import java.util.Collection;
import java.util.Optional;

public interface EventStorage {
    Collection<Event> getEventsByUserId(Long id);

    Long create(Long event_timestamp, Long user_id, EventType event_type, Operation operation, Long entity_id);

    Event getEventById(Long id);
}
