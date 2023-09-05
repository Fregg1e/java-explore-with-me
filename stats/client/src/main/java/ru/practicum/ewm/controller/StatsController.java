package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatsController {
    @PostMapping("/hit")
    public void saveHit(EndpointHitDto endpointHitDto) {

    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris") String[] uris,
            @RequestParam(value = "unique") Boolean unique) {
        return null;
    }
}
