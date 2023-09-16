package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.exception.model.EventDateException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.EventPrivateService;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventPrivateService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;


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
}
