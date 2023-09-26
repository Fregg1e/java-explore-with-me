package ru.practicum.ewm.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsClient {
    private final RestTemplate restTemplate;

    public StatsClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto endpointHitDto) {
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, makeHeaders());

        ResponseEntity<Object> response;
        try {
            response = restTemplate.exchange("/hit", HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }

        HttpStatus responseStatus = response.getStatusCode();
        if (responseStatus.is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(responseStatus);
        if (response.hasBody()) {
            responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String url = "/stats?start={start}&end={end}";
        Map<String, Object> parameters = new HashMap<>(Map.of(
                "start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
        if (uris != null) {
            url += "&uris={uris}";
            parameters.put("uris", uris);
        }
        if (unique != null) {
            url += "&unique={unique}";
            parameters.put("unique", unique);
        }
        HttpEntity<?> requestEntity = new HttpEntity<>(makeHeaders());
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {}, parameters).getBody();
    }

    private HttpHeaders makeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
