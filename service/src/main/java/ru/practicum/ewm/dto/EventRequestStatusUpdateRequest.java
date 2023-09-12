package ru.practicum.ewm.dto;

import lombok.Data;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
