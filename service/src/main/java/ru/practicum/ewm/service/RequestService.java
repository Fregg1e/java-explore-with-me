package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);
}
