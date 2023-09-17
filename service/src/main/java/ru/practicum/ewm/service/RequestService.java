package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);
}
