package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.NewCompilationDto;
import ru.practicum.ewm.dto.UpdateCompilationRequest;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationAdminService;
import ru.practicum.ewm.service.CompilationPublicService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationAdminService, CompilationPublicService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null) {
            for (Long eventId : newCompilationDto.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                                String.format("Событие с ID = %d не существует.", eventId)));
                events.add(event);
            }
        }
        Compilation compilation = Compilation.builder()
                .events(events)
                .pinned(newCompilationDto.getPinned() != null && newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
        try {
            compilation = compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Title должен быть уникальным.",
                    "Подборка с таким title уже существует!");
        }
        log.debug("Создана подборка: " + compilation);
        return CompilationMapper.fromCompilationToCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена.",
                        String.format("Подборки с ID = %d не существует.", compId)));
        compilationRepository.deleteById(compilation.getId());
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена.",
                        String.format("Подборки с ID = %d не существует.", compId)));
        if (updateCompilationRequest.getEvents() != null) {
            List<Event> events = new ArrayList<>();
            for (Long eventId : updateCompilationRequest.getEvents()) {
                Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                                String.format("Событие с ID = %d не существует.", eventId)));
                events.add(event);
            }
            compilation.setEvents(events);
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        return CompilationMapper.fromCompilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations = compilationRepository.getCompilations(pinned,
                new OffsetPageRequest(from, size));
        return compilations.stream().map(CompilationMapper::fromCompilationToCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка не найдена.",
                        String.format("Подборки с ID = %d не существует.", compId)));
        return CompilationMapper.fromCompilationToCompilationDto(compilation);
    }
}
