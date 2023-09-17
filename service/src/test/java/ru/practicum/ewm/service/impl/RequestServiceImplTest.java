package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.RequestException;
import ru.practicum.ewm.model.*;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplTest {
    private final EntityManager entityManager;
    private final RequestServiceImpl requestService;

    @Test
    void createRequestTest_whenModerationIsFalse_thenRequestStatusIsConfirmed() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester = User.builder().name("requester").email("requester@email.com").build();
        entityManager.persist(requester);
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
                .participantLimit(0)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);

        ParticipationRequestDto requestDto = requestService.createRequest(requester.getId(), event.getId());

        assertEquals(EventRequestStatus.CONFIRMED, requestDto.getStatus());
    }

    @Test
    void createRequestTest_whenModerationIsTrue_thenRequestStatusIsPending() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester = User.builder().name("requester").email("requester@email.com").build();
        entityManager.persist(requester);
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
                .participantLimit(0)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);

        ParticipationRequestDto requestDto = requestService.createRequest(requester.getId(), event.getId());

        assertEquals(EventRequestStatus.PENDING, requestDto.getStatus());
    }

    @Test
    void createRequestTest_whenEventNotPublished_thenRequestException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester = User.builder().name("requester").email("requester@email.com").build();
        entityManager.persist(requester);
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
                .participantLimit(0)
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);

        assertThrows(RequestException.class, () -> requestService.createRequest(requester.getId(), event.getId()));
    }

    @Test
    void createRequestTest_whenUserIsInitiator_thenRequestException() {
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
                .participantLimit(0)
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);

        assertThrows(RequestException.class, () -> requestService.createRequest(user.getId(), event.getId()));
    }

    @Test
    void createRequestTest_whenRequestIsExists_thenAlreadyExistException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester = User.builder().name("requester").email("requester@email.com").build();
        entityManager.persist(requester);
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
                .participantLimit(0)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester.getId())
                .build();
        entityManager.persist(request);

        assertThrows(AlreadyExistException.class,
                () -> requestService.createRequest(requester.getId(), event.getId()));
    }

    @Test
    void createRequestTest_whenArrivedLimitRequest_thenAlreadyExistException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
        User requester2 = User.builder().name("requester2").email("requester2@email.com").build();
        entityManager.persist(requester2);
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
                .participantLimit(1)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request);

        assertThrows(RequestException.class, () -> requestService.createRequest(requester2.getId(), event.getId()));
    }
}