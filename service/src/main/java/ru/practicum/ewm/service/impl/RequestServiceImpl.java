package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.EventRequestUpdateStatus;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.exception.model.RequestException;
import ru.practicum.ewm.exception.model.RequestStatusException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new RequestException("Невозможно создать запрос.", "Событие еще не опубликовано.");
        }
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new RequestException("Невозможно создать запрос.", "Пользователь является инициатором события.");
        }
        if (requestRepository.getRequestByUserIdAndEventId(userId, eventId) != null) {
            throw new AlreadyExistException("Невозможно создать запрос.",
                    String.format("Запрос от пользователя с ID = %d для события с ID = %d уже существует.",
                            user.getId(), event.getId()));
        }
        Integer participantLimit = event.getParticipantLimit();
        Integer confirmedRequests = requestRepository.getCountApprovedRequestsByEventId(event.getId());
        if (!participantLimit.equals(0) && !(confirmedRequests < participantLimit)) {
            throw new RequestException("Невозможно создать запрос.", "Достигнут лимит запросов на участие.");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .requester(user.getId())
                .event(event.getId())
                .status(!event.getRequestModeration() || event.getParticipantLimit() == 0
                        ? EventRequestStatus.CONFIRMED : EventRequestStatus.PENDING)
                .build();
        return RequestMapper.fromRequestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден.",
                        String.format("Запрос с ID = %d не существует.", requestId)));
        if (!request.getRequester().equals(user.getId())) {
            throw new NotFoundException("Запрос не найден.",
                    String.format("Запрос с ID = %d не существует.", requestId));
        }
        request.setStatus(EventRequestStatus.CANCELED);
        return RequestMapper.fromRequestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        return requestRepository.getRequestsByUserId(user.getId()).stream()
                .map(RequestMapper::fromRequestToParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getInitiator().getId().equals(user.getId())) {
            return Collections.emptyList();
        }
        return requestRepository.getRequestsByEventId(event.getId()).stream()
                .map(RequestMapper::fromRequestToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult changeEventRequestsStatus(Long userId, Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException("Запросы не найдены.", "Таких запросов не существует.");
        }
        if (!event.getRequestModeration()) {
            throw new RequestException("Невозможно подтвердить запросы.", "Подтверждение запросов не требуется.");
        }
        EventRequestUpdateStatus eventRequestUpdateStatus = eventRequestStatusUpdateRequest.getStatus();
        List<Long> requestIds = eventRequestStatusUpdateRequest.getRequestIds();
        Integer participantLimit = event.getParticipantLimit();
        int remainingRequestLimit = -1;
        boolean isConfirming = eventRequestUpdateStatus.equals(EventRequestUpdateStatus.CONFIRMED);
        if (isConfirming) {
            Integer confirmedRequests = requestRepository.getCountApprovedRequestsByEventId(event.getId());
            if (!participantLimit.equals(0)) {
                if (!(confirmedRequests < participantLimit)) {
                    throw new RequestException("Невозможно подтвердить запросы.",
                            "Достигнут лимит запросов на участие.");
                }
                remainingRequestLimit = participantLimit - confirmedRequests;
                if (remainingRequestLimit < requestIds.size()) {
                    throw new RequestException("Невозможно подтвердить запросы.",
                            "Оставшийся лимит запросов меньше количества данных запросов.");
                }
            }
        }
        List<Request> changeableRequests = requestRepository.getRequestsByRequestIds(requestIds);
        if (changeableRequests.size() < requestIds.size()) {
            List<Long> changeableRequestIds = changeableRequests.stream()
                    .map(Request::getId).collect(Collectors.toList());
            for (Long requestId : requestIds) {
                if (!changeableRequestIds.contains(requestId)) {
                    throw new NotFoundException("Запросы не найдены.",
                            "Запросов со следующими Id не существует:" + requestIds);
                }
            }
        }
        for (Request request : changeableRequests) {
            if (!request.getStatus().equals(EventRequestStatus.PENDING)) {
                throw new RequestStatusException("Невозможно обновить запросы", "Запросы должны иметь статус PENDING");
            }
            request.setStatus(EventRequestStatus.valueOf(eventRequestUpdateStatus.toString()));
            if (!participantLimit.equals(0) && isConfirming) {
                remainingRequestLimit--;
            }
        }
        if (remainingRequestLimit == 0) {
            List<Request> pendingRequests = requestRepository.getRequestsByEventIdAndStatus(event.getId(),
                    EventRequestStatus.PENDING);
            for (Request request : pendingRequests) {
                request.setStatus(EventRequestStatus.REJECTED);
                requestRepository.save(request);
            }
        }
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestRepository.getRequestsByEventIdAndStatus(event.getId(),
                        EventRequestStatus.CONFIRMED).stream()
                        .map(RequestMapper::fromRequestToParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(requestRepository.getRequestsByEventIdAndStatus(event.getId(),
                                EventRequestStatus.REJECTED).stream()
                        .map(RequestMapper::fromRequestToParticipationRequestDto).collect(Collectors.toList()))
                .build();
    }
}
