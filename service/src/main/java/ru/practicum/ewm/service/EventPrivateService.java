package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventUserRequest;

import java.util.List;

public interface EventPrivateService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId,
            UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);
}
