package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class EventShortDto {
    private String annotation;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private Long id;
    private Object initiator;
    private Boolean paid;
    private String title;
    private Integer views;
}
