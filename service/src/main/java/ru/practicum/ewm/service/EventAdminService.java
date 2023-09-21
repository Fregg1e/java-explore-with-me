package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventAdminService {
    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
