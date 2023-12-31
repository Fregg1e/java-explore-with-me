package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.service.*;

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
    private final CategoryAdminService categoryAdminService;
    private final EventAdminService eventAdminService;
    private final CompilationAdminService compilationAdminService;
    private final CommentAdminService commentAdminService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        return categoryAdminService.createCategory(newCategoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long catId) {
        categoryAdminService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable("catId") Long catId, @RequestBody @Valid CategoryDto categoryDto) {
        return categoryAdminService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(@RequestParam(value = "users", required = false) List<Long> users,
            @RequestParam(value = "states", required = false) List<EventState> states,
            @RequestParam(value = "categories", required = false) List<Long> categories,
            @RequestParam(value = "rangeStart", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero  Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return eventAdminService.getEventsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable("eventId") Long eventId,
            @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return eventAdminService.updateEventAdmin(eventId, updateEventAdminRequest);
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
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return compilationAdminService.createCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        compilationAdminService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable("compId") Long compId,
            @RequestBody @Valid UpdateCompilationRequest updateCompilationRequest) {
        return compilationAdminService.updateCompilation(compId, updateCompilationRequest);
    }

    @GetMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByEventId(@PathVariable("eventId") Long eventId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero  Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return commentAdminService.getCommentsByEventId(eventId, from, size);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable("commentId") Long commentId) {
        commentAdminService.deleteCommentByAdmin(commentId);
    }

    @GetMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentById(@PathVariable("commentId") Long commentId) {
        return commentAdminService.getCommentById(commentId);
    }
}
