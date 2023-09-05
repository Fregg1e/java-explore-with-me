package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class ViewStatsDto {
    String app;
    String uri;
    Integer hits;
}
