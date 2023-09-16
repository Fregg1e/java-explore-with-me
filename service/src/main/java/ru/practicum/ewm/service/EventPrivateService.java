package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;

public interface EventPrivateService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);
}
