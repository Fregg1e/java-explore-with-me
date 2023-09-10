package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitMapper {
    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto) {
        LocalDateTime timestamp = LocalDateTime.parse(endpointHitDto.getTimestamp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(timestamp)
                .build();
    }
}
