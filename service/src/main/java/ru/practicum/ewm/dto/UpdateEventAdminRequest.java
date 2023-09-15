package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private EventStateAdminUpdate stateAction;
    private String title;
}
