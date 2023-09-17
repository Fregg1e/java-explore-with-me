package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.model.EventDateException;
import ru.practicum.ewm.exception.model.EventStateException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.model.*;

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

    @Test
    void updateEventByUserIdAndEventIdTest_whenUpdate_thenReturnUpdatedEvent() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true).build();

        EventFullDto eventFullDto = eventService.updateEventByUserIdAndEventId(user.getId(), event.getId(),
                updateEventUserRequest);

        assertEquals(updateEventUserRequest.getPaid(), eventFullDto.getPaid());
        assertEquals(event.getAnnotation(), eventFullDto.getAnnotation());
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenUserIsNotOwner_thenNotFoundException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User user2 = User.builder().name("test2").email("test2@email.com").build();
        entityManager.persist(user2);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true).build();

        assertThrows(NotFoundException.class, () -> eventService.updateEventByUserIdAndEventId(user2.getId(),
                event.getId(), updateEventUserRequest));
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenEventIsPublished_thenEventStateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true).build();

        assertThrows(EventStateException.class, () -> eventService.updateEventByUserIdAndEventId(user.getId(),
                event.getId(), updateEventUserRequest));
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenOldDateIsBeforeDeadLine_thenEventDateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(1))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true).build();

        assertThrows(EventDateException.class, () -> eventService.updateEventByUserIdAndEventId(user.getId(),
                event.getId(), updateEventUserRequest));
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenNewDateIsBeforeDeadLine_thenEventDateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true)
                .eventDate(LocalDateTime.now().plusHours(1)).build();

        assertThrows(EventDateException.class, () -> eventService.updateEventByUserIdAndEventId(user.getId(),
                event.getId(), updateEventUserRequest));
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenStateActionIsSendToReview_thenStateIsPending() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.CANCELED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true)
                .stateAction(EventStateUserUpdate.SEND_TO_REVIEW).build();

        EventFullDto eventFullDto = eventService.updateEventByUserIdAndEventId(user.getId(), event.getId(),
                updateEventUserRequest);

        assertEquals(updateEventUserRequest.getPaid(), eventFullDto.getPaid());
        assertEquals(event.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(EventState.PENDING, eventFullDto.getState());
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenStateActionIsCancelReview_thenStateIsCanceled() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventUserRequest updateEventUserRequest = UpdateEventUserRequest.builder().paid(true)
                .stateAction(EventStateUserUpdate.CANCEL_REVIEW).build();

        EventFullDto eventFullDto = eventService.updateEventByUserIdAndEventId(user.getId(), event.getId(),
                updateEventUserRequest);

        assertEquals(updateEventUserRequest.getPaid(), eventFullDto.getPaid());
        assertEquals(event.getAnnotation(), eventFullDto.getAnnotation());
        assertEquals(EventState.CANCELED, eventFullDto.getState());
    }

    @Test
    void updateEventAdminTest_whenPublishEvent_thenStateIsPublished() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventAdminRequest updateEventAdminRequest = UpdateEventAdminRequest.builder()
                .stateAction(EventStateAdminUpdate.PUBLISH_EVENT).build();

        EventFullDto eventFullDto = eventService.updateEventAdmin(event.getId(), updateEventAdminRequest);

        assertEquals(EventState.PUBLISHED, eventFullDto.getState());
    }

    @Test
    void updateEventAdminTest_whenRejectEventAndStateIsPending_thenStateIsCanceled() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventAdminRequest updateEventAdminRequest = UpdateEventAdminRequest.builder()
                .stateAction(EventStateAdminUpdate.REJECT_EVENT).build();

        EventFullDto eventFullDto = eventService.updateEventAdmin(event.getId(), updateEventAdminRequest);

        assertEquals(EventState.CANCELED, eventFullDto.getState());
    }

    @Test
    void updateEventAdminTest_whenPublishEventAndStateIsCanceled_thenEventStateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.CANCELED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventAdminRequest updateEventAdminRequest = UpdateEventAdminRequest.builder()
                .stateAction(EventStateAdminUpdate.PUBLISH_EVENT).build();

        assertThrows(EventStateException.class,
                () -> eventService.updateEventAdmin(event.getId(), updateEventAdminRequest));
    }

    @Test
    void updateEventAdminTest_whenPublishEventIsLate_thenEventDateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusMinutes(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventAdminRequest updateEventAdminRequest = UpdateEventAdminRequest.builder()
                .stateAction(EventStateAdminUpdate.PUBLISH_EVENT).build();

        assertThrows(EventDateException.class,
                () -> eventService.updateEventAdmin(event.getId(), updateEventAdminRequest));
    }

    @Test
    void updateEventAdminTest_whenRejectEventAndStateIsPublished_thenEventStateException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        Category category = Category.builder().name("test").build();
        entityManager.persist(category);
        Location location = Location.builder().lat(55.754167F).lon(37.62F).build();
        entityManager.persist(location);
        Event event = Event.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(category)
                .createdOn(LocalDateTime.now().minusDays(2))
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusHours(20))
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        UpdateEventAdminRequest updateEventAdminRequest = UpdateEventAdminRequest.builder()
                .stateAction(EventStateAdminUpdate.REJECT_EVENT).build();

        assertThrows(EventStateException.class,
                () -> eventService.updateEventAdmin(event.getId(), updateEventAdminRequest));
    }
}