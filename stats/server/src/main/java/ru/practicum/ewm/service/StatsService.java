package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;

public interface StatsService {
    void saveHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(String start, String end, String[] uris, Boolean unique);
}
