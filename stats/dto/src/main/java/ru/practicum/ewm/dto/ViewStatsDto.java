package ru.practicum.ewm.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ViewStatsDto {
    String app;
    String uri;
    Integer hits;
}
