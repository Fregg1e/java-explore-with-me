package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.repository.EndpointHitRepository;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        log.debug("Save: " + endpointHitDto);
        endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            log.debug("Return unique stats with param: " + start + ", " + end + ", " + uris);
            return endpointHitRepository.getUniqueStats(start, end, uris).stream().map(
                    ViewStatsMapper::toViewStatsDto).collect(Collectors.toList());
        } else {
            log.debug("Return stats with param: " + start + ", " + end + ", " + uris);
            return endpointHitRepository.getStats(start, end, uris).stream().map(
                    ViewStatsMapper::toViewStatsDto).collect(Collectors.toList());
        }
    }
}
