package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PublicController {
    @GetMapping("/compilations")
    public List<CompilationDto> getCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "from", defaultValue = "0")  Integer from,
            @RequestParam(value = "size", defaultValue = "10")  Integer size) {
        return null;
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getCompilationById(@PathVariable("compId") Long compId) {
        return null;
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0")  Integer from,
            @RequestParam(value = "size", defaultValue = "10")  Integer size) {
        return null;
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Long catId) {
        return null;
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "paid", required = false) Boolean paid,
            @RequestParam(value = "rangeStart", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "text", defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(value = "text", defaultValue = "VIEWS") EventSort sort,
            @RequestParam(value = "from", defaultValue = "0")  Integer from,
            @RequestParam(value = "size", defaultValue = "10")  Integer size) {
        return null;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long id) {
        return null;
    }
}
