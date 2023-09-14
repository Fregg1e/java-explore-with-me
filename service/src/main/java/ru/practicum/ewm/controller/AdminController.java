package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.UserAdminService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Validated
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserAdminService userAdminService;

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
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getUsers(@RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return userAdminService.getUsers(ids, from, size);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userAdminService.createUser(newUserRequest);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("userId") Long userId) {
        userAdminService.deleteUser(userId);
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
