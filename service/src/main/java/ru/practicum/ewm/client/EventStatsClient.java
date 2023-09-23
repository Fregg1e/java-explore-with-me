package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventStatsClient extends StatsClient {
    public EventStatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public Map<Long, Long> getViewsByIds(List<Long> ids) {
        String[] uris = ids.stream().map(id -> "/events/" + id).toArray(String[]::new);
        List<ViewStatsDto> viewStatsDtos = super.getStats(LocalDateTime.MIN, LocalDateTime.now(), uris, true);
        Map<Long, Long> views = viewStatsDtos.stream().collect(Collectors.toMap(
                viewStatsDto -> Long.parseLong(viewStatsDto.getUri()
                        .substring(viewStatsDto.getUri().length() - 1)),
                ViewStatsDto::getHits
        ));
        return null;
    }
}
