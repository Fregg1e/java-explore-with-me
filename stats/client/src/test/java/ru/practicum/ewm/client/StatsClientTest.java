package ru.practicum.ewm.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StatsClientTest {
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private StatsClient client;

    @Test
    void saveHitTest_whenSaveHit_thenStatusIsAccepted() {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp("2022-09-06 11:00:23").build();

        Mockito.when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.ACCEPTED));

        ResponseEntity<Object> response = client.saveHit(endpointHitDto);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(Object.class));
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Test
    void saveHitTest_whenServerSendError_thenStatusIsSendingError() {
        EndpointHitDto endpointHitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .timestamp("2022-09-06 11:00:23").build();

        Mockito.when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<Object> response = client.saveHit(endpointHitDto);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(), eq(Object.class));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void getStats() {
        ViewStatsDto viewStatsDto = ViewStatsDto.builder().app("ewm-main-service")
                .uri("/events/1")
                .hits(2L).build();

        Mockito.when(restTemplate.exchange(anyString(), any(), any(),
                        eq(new ParameterizedTypeReference<List<ViewStatsDto>>() {}), anyMap()))
                .thenReturn(new ResponseEntity<>(List.of(viewStatsDto), HttpStatus.OK));

        List<ViewStatsDto> viewStatsDtos = client.getStats(LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1), null, null);
        verify(restTemplate, times(1)).exchange(anyString(), any(), any(),
                eq(new ParameterizedTypeReference<List<ViewStatsDto>>() {}), anyMap());
        assertEquals(viewStatsDto, viewStatsDtos.get(0));
    }
}