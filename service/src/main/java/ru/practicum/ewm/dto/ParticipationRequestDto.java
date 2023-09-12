package ru.practicum.ewm.dto;

import lombok.Data;
import ru.practicum.ewm.model.RequestStatus;

@Data
public class ParticipationRequestDto {
    private String created;
    private Long event;
    private Long id;
    private Long requester;
    private RequestStatus status;
}
