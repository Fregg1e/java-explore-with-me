package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CompilationDto;

import java.util.List;

public interface CompilationPublicService {
    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
