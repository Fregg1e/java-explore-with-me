package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    @PostMapping("/categories")
    public CategoryDto createCategory(@RequestBody NewCategoryDto newCategoryDto) {
        return null;
    }

    @DeleteMapping("/categories/{catId}")
    public void deleteCategory(@PathVariable("catId") Long catId) {

    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") Long catId, @RequestBody CategoryDto categoryDto) {
        return null;
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(@RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<EventState> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return null;
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("eventId") Long eventId,
            @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        return null;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return null;
    }

    @PostMapping("/users")
    public UserDto createUser(@RequestBody NewUserRequest newUserRequest) {
        return null;
    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable("userId") Long userId) {

    }

    @PostMapping("/compilations")
    public CompilationDto createCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        return null;
    }

    @DeleteMapping("/compilations/{compId}")
    public void deleteCompilation(@PathVariable("compId") Long compId) {

    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Long compId,
            @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return null;
    }
}
