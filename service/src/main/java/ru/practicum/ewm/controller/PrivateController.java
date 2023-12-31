package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CommentPrivateService;
import ru.practicum.ewm.service.EventPrivateService;
import ru.practicum.ewm.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateController {
    private final EventPrivateService eventPrivateService;
    private final RequestService requestService;
    private final CommentPrivateService commentPrivateService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsByUserId(@PathVariable("userId") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return eventPrivateService.getEventsByUserId(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable("userId") Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventPrivateService.createEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        return eventPrivateService.getEventByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByUserIdAndEventId(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return eventPrivateService.updateEventByUserIdAndEventId(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changeEventRequestsStatus(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return requestService.changeEventRequestsStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable("userId") Long userId) {
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
            @RequestParam(value = "eventId") Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PostMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable("userId") Long userId,
            @RequestParam(value = "eventId") Long eventId, @RequestBody @Valid CommentDto commentDto) {
        return commentPrivateService.createComment(userId, eventId, commentDto);
    }

    @PatchMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateComment(@PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId, @RequestBody @Valid CommentDto commentDto) {
        return commentPrivateService.updateComment(userId, commentId, commentDto);
    }

    @DeleteMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId) {
        commentPrivateService.deleteComment(userId, commentId);
    }

    @GetMapping("/{userId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUserId(@PathVariable("userId") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        return commentPrivateService.getCommentsByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByUserIdAndCommentId(@PathVariable("userId") Long userId,
            @PathVariable("commentId") Long commentId) {
        return commentPrivateService.getCommentByUserIdAndCommentId(userId, commentId);
    }
}
