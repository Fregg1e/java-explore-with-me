package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateController {
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUserId(@PathVariable("userId") Long userId,
            @RequestParam(value = "from", defaultValue = "0")  Integer from,
            @RequestParam(value = "size", defaultValue = "10")  Integer size) {
        return null;
    }

    @PostMapping("/{userId}/events")
    public EventFullDto createEvent(@PathVariable("userId") Long userId, @RequestBody NewEventDto newEventDto) {
        return null;
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventByUserIdAndEventId(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        return null;
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUserIdAndEventId(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return null;
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequests(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {
        return null;
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeEventRequestsStatus(@PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return null;
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable("userId") Long userId) {
        return null;
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createRequest(@PathVariable("userId") Long userId,
            @RequestParam(value = "eventId ")  Long eventId ) {
        return null;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
            @PathVariable("requestId") Long requestId) {
        return null;
    }
}
