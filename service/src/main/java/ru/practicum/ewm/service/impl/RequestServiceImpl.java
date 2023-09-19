package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.exception.model.RequestException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.RequestService;

import java.time.LocalDateTime;
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
                .status(event.getRequestModeration() ? EventRequestStatus.PENDING : EventRequestStatus.CONFIRMED)
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
        request.setStatus(EventRequestStatus.REJECTED);
        return RequestMapper.fromRequestToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        return requestRepository.getRequestsByUserId(user.getId()).stream()
                .map(RequestMapper::fromRequestToParticipationRequestDto).collect(Collectors.toList());
    }
}
