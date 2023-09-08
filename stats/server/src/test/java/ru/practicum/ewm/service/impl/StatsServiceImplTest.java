package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.mapper.EndpointHitMapper;
import ru.practicum.ewm.model.EndpointHit;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class StatsServiceImplTest {
    private final EntityManager entityManager;
    private final StatsServiceImpl service;

    @Test
    void saveHitTest() {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp("2022-09-06 11:00:23")
                .build();
        service.saveHit(endpointHitDto);

        TypedQuery<EndpointHit> query = entityManager
                .createQuery("Select eh from EndpointHit eh where eh.uri = :uri", EndpointHit.class);
        EndpointHit endpointHit = query
                .setParameter("uri", endpointHitDto.getUri())
                .getSingleResult();

        assertNotNull(endpointHit);
        assertEquals(endpointHitDto.getApp(), endpointHit.getApp());
        assertEquals(endpointHitDto.getUri(), endpointHit.getUri());
        assertEquals(endpointHitDto.getIp(), endpointHit.getIp());
        assertEquals(endpointHitDto.getTimestamp(), endpointHit.getTimestamp()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @Test
    void getStatsTest() {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.1")
                .timestamp("2022-09-06 11:00:23")
                .build();
        entityManager.persist(EndpointHitMapper.toEndpointHit(endpointHitDto));
        String start = URLEncoder.encode("2020-09-06 11:00:23", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("2023-09-06 11:00:23", StandardCharsets.UTF_8);

        List<ViewStatsDto> viewStatsDtos = service.getStats(start, end, null, false);

        assertEquals(endpointHitDto.getApp(), viewStatsDtos.get(0).getApp());
        assertEquals(endpointHitDto.getUri(), viewStatsDtos.get(0).getUri());
        assertEquals(1L, viewStatsDtos.get(0).getHits());
    }

    @Test
    void getStatsUniqueTest() {
        EndpointHitDto endpointHitDto1 = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/3")
                .ip("192.163.0.1")
                .timestamp("2022-09-06 11:00:23")
                .build();
        entityManager.persist(EndpointHitMapper.toEndpointHit(endpointHitDto1));
        EndpointHitDto endpointHitDto2 = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/3")
                .ip("192.163.0.1")
                .timestamp("2022-09-07 11:00:23")
                .build();
        entityManager.persist(EndpointHitMapper.toEndpointHit(endpointHitDto2));
        String start = URLEncoder.encode("2020-09-06 11:00:23", StandardCharsets.UTF_8);
        String end = URLEncoder.encode("2023-09-06 11:00:23", StandardCharsets.UTF_8);

        List<ViewStatsDto> viewStatsDtos = service.getStats(start, end, List.of("/events/3"), true);

        assertEquals(endpointHitDto1.getApp(), viewStatsDtos.get(0).getApp());
        assertEquals(endpointHitDto1.getUri(), viewStatsDtos.get(0).getUri());
        assertEquals(1L, viewStatsDtos.get(0).getHits());
    }
}