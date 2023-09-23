package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventStatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.model.EventDateException;
import ru.practicum.ewm.exception.model.EventStateException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventAdminService;
import ru.practicum.ewm.service.EventPrivateService;
import ru.practicum.ewm.service.EventPublicService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventPrivateService, EventAdminService, EventPublicService {
    private final static String APP_NAME = "ewm-main-service";
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final EventStatsClient eventStatsClient;


    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория не найдена.",
                        String.format("Категория с ID = %d не существует.", newEventDto.getCategory())));
        Float lat = newEventDto.getLocation().getLat();
        Float lon = newEventDto.getLocation().getLon();
        Location location = locationRepository.getLocationByLatAndLon(lat, lon);
        if (location == null) {
            location = locationRepository.save(Location.builder().lat(lat).lon(lon).build());
        }
        LocalDateTime createdOn = LocalDateTime.now();
        if (newEventDto.getEventDate().isBefore(createdOn.plusHours(2))) {
            throw new EventDateException("Не возможно создать событие.", "Неправильная дата начала ивента.");
        }
        Event event = EventMapper.fromNewEventDtoToEvent(newEventDto);
        event.setCategory(category);
        event.setCreatedOn(createdOn);
        event.setInitiator(user);
        event.setLocation(location);
        event.setState(EventState.PENDING);
        log.debug("Создание события: " + event);
        return EventMapper.fromEventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId,
            UpdateEventUserRequest updateEventUserRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException("Событие не найдено.",
                    String.format("Событие с ID = %d не существует.", eventId));
        }
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateException("Событие уже опубликовано.",
                    String.format("Событие с ID = %d опубликовано.", eventId));
        }
        LocalDateTime deadLine =  LocalDateTime.now().plusHours(2);
        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(deadLine)) {
                throw new EventDateException("Не возможно обновить событие.", "Неправильная дата начала ивента.");
            }
            event.setEventDate(updateEventUserRequest.getEventDate());
        } else {
            if (event.getEventDate().isBefore(deadLine)) {
                throw new EventDateException("Не возможно обновить событие.", "Неправильная дата начала ивента.");
            }
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            setNewCategory(event, updateEventUserRequest.getCategory());
        }
        if (updateEventUserRequest.getLocation() != null) {
            Float lat = updateEventUserRequest.getLocation().getLat();
            Float lon = updateEventUserRequest.getLocation().getLon();
            setLocation(event, lat, lon);
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(EventStateUserUpdate.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        EventFullDto eventFullDto = EventMapper.fromEventToEventFullDto(eventRepository.save(event));
        eventFullDto.setConfirmedRequests(getConfirmedRequest(eventFullDto.getId()));
        setViewsEventFullDto(List.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        List<EventShortDto> eventShortDtos = eventRepository.getEventsByUserId(user.getId(),
                        new OffsetPageRequest(from, size))
                .stream().map(EventMapper::fromEventToEventShortDto)
                .peek(e -> e.setConfirmedRequests(getConfirmedRequest(e.getId())))
                .collect(Collectors.toList());
        setViewsEventShortDto(eventShortDtos);
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getInitiator().getId().equals(user.getId())) {
            throw new NotFoundException("Событие не найдено.",
                    String.format("Событие с ID = %d не существует.", eventId));
        }
        EventFullDto eventFullDto = EventMapper.fromEventToEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequest(eventFullDto.getId()));
        setViewsEventFullDto(List.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    public EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            setNewCategory(event, updateEventAdminRequest.getCategory());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            Float lat = updateEventAdminRequest.getLocation().getLat();
            Float lon = updateEventAdminRequest.getLocation().getLon();
            setLocation(event, lat, lon);
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(EventStateAdminUpdate.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new EventStateException("Событие не возможно опубликовать.",
                            String.format("Событие с ID = %d не ожидает публикации.", eventId));
                }
                LocalDateTime publishedOn = LocalDateTime.now();
                if (event.getEventDate().isBefore(publishedOn.plusHours(1))) {
                    throw new EventDateException("Не возможно обновить событие.",
                            "Период доступности публикации прошел.");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(publishedOn);
            } else {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new EventStateException("Событие не возможно отклонить.",
                            String.format("Событие с ID = %d уже опубликовано.", eventId));
                }
                event.setState(EventState.CANCELED);
            }
        }
        EventFullDto eventFullDto = EventMapper.fromEventToEventFullDto(eventRepository.save(event));
        eventFullDto.setConfirmedRequests(getConfirmedRequest(eventFullDto.getId()));
        setViewsEventFullDto(List.of(eventFullDto));
        return eventFullDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getEventsAdmin(List<Long> users, List<EventState> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<EventFullDto> eventFullDtos = eventRepository.getEventsAdmin(users, states, categories, rangeStart,
                        rangeEnd, new OffsetPageRequest(from, size))
                .stream().map(EventMapper::fromEventToEventFullDto)
                .peek(e -> e.setConfirmedRequests(getConfirmedRequest(e.getId())))
                .collect(Collectors.toList());
        setViewsEventFullDto(eventFullDtos);
        return eventFullDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size,
            HttpServletRequest request) {
        List<Event> events;
        if (rangeStart == null && rangeEnd == null) {
            events = eventRepository.getEvents(text, categories, paid, LocalDateTime.now(), null,
                    new OffsetPageRequest(from, size));
        } else {
            events = eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd,
                    new OffsetPageRequest(from, size));
        }
        List<EventShortDto> eventShortDtos = new ArrayList<>();
        for (Event event : events) {
            Integer confirmedRequest = getConfirmedRequest(event.getId());
            if (onlyAvailable && confirmedRequest.equals(event.getParticipantLimit())) {
                continue;
            }
            EventShortDto eventShortDto = EventMapper.fromEventToEventShortDto(event);
            eventShortDto.setConfirmedRequests(confirmedRequest);
            eventShortDtos.add(eventShortDto);
        }
        setViewsEventShortDto(eventShortDtos);
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventShortDtos = eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getEventDate))
                    .collect(Collectors.toList());
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventShortDtos = eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(Collectors.toList());
        }
        eventStatsClient.saveHit(EndpointHitDto.builder()
                .app(APP_NAME)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());
        return eventShortDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено.",
                        String.format("Событие с ID = %d не существует.", eventId)));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Событие не найдено.",
                    String.format("Событие с ID = %d не существует.", event.getId()));
        }
        EventFullDto eventFullDto = EventMapper.fromEventToEventFullDto(event);
        eventFullDto.setConfirmedRequests(getConfirmedRequest(event.getId()));
        eventStatsClient.saveHit(EndpointHitDto.builder()
                .app(APP_NAME)
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build());
        setViewsEventFullDto(List.of(eventFullDto));
        return eventFullDto;
    }

    private void setNewCategory(Event event, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория не найдена.",
                        String.format("Категория с ID = %d не существует.", categoryId)));
        event.setCategory(category);
    }

    private void setLocation(Event event, Float lat, Float lon) {
        Location location = locationRepository.getLocationByLatAndLon(lat, lon);
        if (location == null) {
            location = locationRepository.save(Location.builder().lat(lat).lon(lon).build());
        }
        event.setLocation(location);
    }

    private Integer getConfirmedRequest(Long eventId) {
        Integer confirmedRequest = requestRepository.getCountApprovedRequestsByEventId(eventId);
        if (confirmedRequest == null) {
            confirmedRequest = 0;
        }
        return confirmedRequest;
    }

    private void setViewsEventShortDto(List<EventShortDto> eventShortDtos) {
        if (!eventShortDtos.isEmpty()) {
            Map<Long, Long> views = eventStatsClient.getViewsByIds(eventShortDtos.stream().map(EventShortDto::getId)
                    .collect(Collectors.toList()));
            for (EventShortDto eventShortDto : eventShortDtos) {
                Long view = views.get(eventShortDto.getId());
                eventShortDto.setViews(Objects.requireNonNullElse(view, 0L));
            }
        }
    }

    private void setViewsEventFullDto(List<EventFullDto> eventFullDtos) {
        if (!eventFullDtos.isEmpty()) {
            Map<Long, Long> views = eventStatsClient.getViewsByIds(eventFullDtos.stream().map(EventFullDto::getId)
                    .collect(Collectors.toList()));
            for (EventFullDto eventFullDto : eventFullDtos) {
                Long view = views.get(eventFullDto.getId());
                eventFullDto.setViews(Objects.requireNonNullElse(view, 0L));
            }
        }
    }
}
