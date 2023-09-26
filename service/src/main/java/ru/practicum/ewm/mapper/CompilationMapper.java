package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.model.Compilation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompilationMapper {
    public static CompilationDto fromCompilationToCompilationDto(Compilation compilation) {
        List<EventShortDto> eventShortDtos;
        if (compilation.getEvents() == null) {
            eventShortDtos = Collections.emptyList();
        } else {
            eventShortDtos = compilation.getEvents().stream()
                    .map(EventMapper::fromEventToEventShortDto)
                    .collect(Collectors.toList());
        }
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(eventShortDtos)
                .build();
    }
}
