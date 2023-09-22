package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CompilationPublicService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequiredArgsConstructor
public class PublicController {
    private final CompilationPublicService compilationPublicService;

    @GetMapping("/compilations")
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1)  Integer size) {
        return compilationPublicService.getCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable("compId") Long compId) {
        return compilationPublicService.getCompilationById(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(value = "from", defaultValue = "0")
                @PositiveOrZero  Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
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
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size, HttpServletRequest request) {
        return null;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable("id") Long id, HttpServletRequest request) {
        return null;
    }
}
