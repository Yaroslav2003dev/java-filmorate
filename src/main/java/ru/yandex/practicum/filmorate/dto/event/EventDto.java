package ru.yandex.practicum.filmorate.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.service.EventType;
import ru.yandex.practicum.filmorate.service.Operation;

@Data
public class EventDto {
    @JsonProperty(value = "eventId", access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long timestamp;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long userId;
    private EventType eventType;
    private Operation operation;
    private Long entityId;
}
