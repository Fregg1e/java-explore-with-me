package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto fromRequestToParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent())
                .requester(request.getRequester())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
