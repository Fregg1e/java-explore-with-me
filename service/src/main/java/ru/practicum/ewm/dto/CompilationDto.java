package ru.practicum.ewm.dto;

import lombok.Data;

@Data
public class CompilationDto {
    private Object events;
    private Long id;
    private Boolean pinned;
    private String title;
}
