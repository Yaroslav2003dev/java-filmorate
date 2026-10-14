package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Autowired
    EventService(EventStorage eventStorage, UserStorage userStorage) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public Collection<EventDto> getEventsByUserId(Long userId) {
        userStorage.getUserById(userId);
        Collection<Event> events = eventStorage.getEventsByUserId(userId);
        log.info("Сформирован список событий пользователя c id = {} c размером: {}", userId, events.size());
        return events.stream()
                .map(EventMapper::mapToEventDto)
                .collect(Collectors.toList());
    }

    public EventDto createEvent(Long userId, EventType eventType, Operation operation, Long entityId) {
        Long eventTimestamp = Instant.now().toEpochMilli();
        Long eventId = eventStorage.create(eventTimestamp, userId, eventType, operation, entityId);
        return EventMapper.mapToEventDto(eventStorage.getEventById(eventId));
    }
}
