package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.model.EventRequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationRequestDto {
    private LocalDateTime created;
    private Long event;
    private Long id;
    private Long requester;
    private EventRequestStatus status;
}
