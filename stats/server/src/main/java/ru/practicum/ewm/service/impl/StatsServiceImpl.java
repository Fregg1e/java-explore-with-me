package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.mapper.ViewStatsMapper;
import ru.practicum.ewm.repository.EndpointHitRepository;
import ru.practicum.ewm.service.StatsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final EndpointHitRepository endpointHitRepository;

    @Override
    public void saveHit(EndpointHitDto endpointHitDto) {
        endpointHitRepository.save(EndpointHitMapper.toEndpointHit(endpointHitDto));
    }

    @Override
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime decodedStart = LocalDateTime.parse(URLDecoder.decode(start, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime decodedEnd = LocalDateTime.parse(URLDecoder.decode(end, StandardCharsets.UTF_8),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if (unique) {
            return endpointHitRepository.getUniqueStats(decodedStart, decodedEnd, uris).stream().map(
                    ViewStatsMapper::toViewStatsDto).collect(Collectors.toList());
        } else {
            return endpointHitRepository.getStats(decodedStart, decodedEnd, uris).stream().map(
                    ViewStatsMapper::toViewStatsDto).collect(Collectors.toList());
        }
    }
}
