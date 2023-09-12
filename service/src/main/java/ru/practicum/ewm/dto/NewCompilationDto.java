package ru.practicum.ewm.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Location> events;
    private Boolean pinned;
    private String title;
}
