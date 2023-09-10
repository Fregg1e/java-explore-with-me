package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.ViewStats;

public class ViewStatsMapper {
    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .app(viewStats.getApp())
                .uri(viewStats.getUri())
                .hits(viewStats.getHits())
                .build();
    }
}
