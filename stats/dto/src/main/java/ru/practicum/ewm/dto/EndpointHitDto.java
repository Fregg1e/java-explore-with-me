package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EndpointHitDto {
    Long id;
    String app;
    String uri;
    String ip;
    String timestamp;
}
