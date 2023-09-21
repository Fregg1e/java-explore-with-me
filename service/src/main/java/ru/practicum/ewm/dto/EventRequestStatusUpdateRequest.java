package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {
    @NotEmpty(message = "Список id не может быть пустым.")
    private List<Long> requestIds;
    @NotNull(message = "Статус не может быть null.")
    private EventRequestUpdateStatus status;
}
