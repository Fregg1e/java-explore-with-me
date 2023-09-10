package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.StatsService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private final ViewStatsDto viewStatsDto = ViewStatsDto.builder()
            .app("ewm-main-service")
            .uri("/events/1")
            .hits(2L)
            .build();

    @Test
    void saveHitTest() throws Exception {
        mockMvc.perform(post("/hit")
                .content(mapper.writeValueAsString(endpointHitDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void getStatsTest() throws Exception {
        LocalDateTime start = LocalDateTime.parse("2022-07-06 11:00:23",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse("2023-07-06 11:00:23",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        when(service.getStats(start, end, null, false)).thenReturn(List.of(viewStatsDto));

        mockMvc.perform(get("/stats")
                .param("start", "2022-07-06 11:00:23")
                .param("end", "2023-07-06 11:00:23")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].app", is(viewStatsDto.getApp()), String.class))
                .andExpect(jsonPath("$[0].uri", is(viewStatsDto.getUri()), String.class))
                .andExpect(jsonPath("$[0].hits", is(viewStatsDto.getHits()), Long.class));
    }

    @Test
    void getStatsTest_whenWithoutDate_thenReturnBadRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getStatsTest_whenServiceThrowRuntimeException_thenReturnInternalServerError() throws Exception {
        LocalDateTime start = LocalDateTime.parse("2022-07-06 11:00:23",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime end = LocalDateTime.parse("2023-07-06 11:00:23",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        when(service.getStats(start, end, null, false)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/stats")
                        .param("start", "2022-07-06 11:00:23")
                        .param("end", "2023-07-06 11:00:23")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}