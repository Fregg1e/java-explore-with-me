package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.exception.model.EventDateException;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.Location;
import ru.practicum.ewm.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final EventServiceImpl eventService;

    @Test
    void createEventTest_whenCreateEvent_thenReturnNewEvent() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category.getId())
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusDays(3))
                .location(LocationDto.builder().lat(55.754167F).lon(37.62F).build())
                .paid(true)
                .participantLimit(10)
                .requestModeration(false)
                .title("Сплав на байдарках")
                .build();

        EventFullDto eventFullDto = eventService.createEvent(user.getId(), newEventDto);

        assertEquals(newEventDto.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(newEventDto.getDescription(), eventFullDto.getDescription());
        assertEquals(newEventDto.getEventDate(), eventFullDto.getEventDate());
        assertEquals(EventState.PENDING, eventFullDto.getState());
    }

    @Test
    void createEventTest_whenLocationIsExists_thenUseExistLocation() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category.getId())
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusDays(3))
                .location(LocationDto.builder().lat(55.754167F).lon(37.62F).build())
                .paid(true)
                .participantLimit(10)
                .requestModeration(false)
                .title("Сплав на байдарках")
                .build();

        EventFullDto eventFullDto = eventService.createEvent(user.getId(), newEventDto);

        TypedQuery<Long> query = entityManager
                .createQuery("Select count(l) from Location l", Long.class);
        Long count = query.getSingleResult();

        assertEquals(newEventDto.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(newEventDto.getDescription(), eventFullDto.getDescription());
        assertEquals(newEventDto.getEventDate(), eventFullDto.getEventDate());
        assertEquals(1L, count);
    }

    @Test
    void createEventTest_whenCreatedOnIsBefore_throwEventDateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category.getId())
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(1))
                .location(LocationDto.builder().lat(55.754167F).lon(37.62F).build())
                .paid(true)
                .participantLimit(10)
                .requestModeration(false)
                .title("Сплав на байдарках")
                .build();

        assertThrows(EventDateException.class, () -> eventService.createEvent(user.getId(), newEventDto));
    }
}