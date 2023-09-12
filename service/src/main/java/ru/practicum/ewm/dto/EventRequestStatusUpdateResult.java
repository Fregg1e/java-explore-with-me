package ru.practicum.ewm.dto;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    private List<Object> confirmedRequests;
    private List<Object> rejectedRequests;
}
