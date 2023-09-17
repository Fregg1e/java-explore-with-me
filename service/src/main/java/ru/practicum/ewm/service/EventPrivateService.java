package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;

public interface EventPrivateService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);
    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId,
            UpdateEventUserRequest updateEventUserRequest);
}
