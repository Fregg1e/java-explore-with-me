package ru.practicum.ewm.service.impl;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

import java.util.List;

public class StatsServiceImpl implements StatsService {
    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {

    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, String[] uris, Boolean unique) {
        return null;
    }
}
