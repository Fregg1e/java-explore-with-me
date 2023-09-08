package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.service.StatsService;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = StatsController.class)
class StatsControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StatsService service;
    private final EndpointHitDto endpointHitDto = EndpointHitDto.builder()
            .app("ewm-main-service")
            .uri("/events/1")
            .ip("192.163.0.1")
            .timestamp("2022-09-06 11:00:23")
            .build();

    @Test
    void saveHitTest() {
    }

    @Test
    void getStatsTest() {
    }
}