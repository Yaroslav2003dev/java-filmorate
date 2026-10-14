package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static EventDto mapToEventDto(Event event) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setEventType(event.getEventType());
        eventDto.setEntityId(event.getEntityId());
        eventDto.setOperation(event.getOperation());
        eventDto.setTimestamp(event.getTimestamp());
        eventDto.setUserId(event.getUserId());
        return eventDto;
    }
}
