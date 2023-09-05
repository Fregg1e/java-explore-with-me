package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class EndpointHitDto {
    Long id;
    String app;
    String uri;
    String ip;
    String timestamp;
}
