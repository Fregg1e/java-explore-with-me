package ru.practicum.ewm.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient {
    private final RestTemplate restTemplate;

    public StatsClient(String statServerUrl) {
        this.restTemplate = new RestTemplateBuilder().uriTemplateHandler(new DefaultUriBuilderFactory(statServerUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
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

    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String encodeStart = URLEncoder.encode(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                StandardCharsets.UTF_8);
        String encodeEnd = URLEncoder.encode(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                StandardCharsets.UTF_8);
        Map<String, Object> parameters = Map.of(
            "start", encodeStart,
            "end", encodeEnd,
            "uris", uris,
            "unique", unique
        );
        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(null, makeHeaders());
        return restTemplate.exchange("/stats", HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<ViewStatsDto>>() {}).getBody();
    }

    private HttpHeaders makeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
