package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class UpdateEventUserRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateUserUpdate stateAction;
    private String title;
}