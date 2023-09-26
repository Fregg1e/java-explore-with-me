package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.EventRequestUpdateStatus;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.exception.model.RequestException;
import ru.practicum.ewm.exception.model.RequestStatusException;
import ru.practicum.ewm.model.*;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceImplIntegrationTest {
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
                .participantLimit(2)
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
                .participantLimit(2)
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

    @Test
    void cancelRequestTest_whenCancel_thenStatusIsRejected() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
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

        ParticipationRequestDto participationRequestDto = requestService.cancelRequest(requester1.getId(),
                request.getId());

        assertEquals(EventRequestStatus.CANCELED, participationRequestDto.getStatus());
    }

    @Test
    void getRequestsByUserIdTest_whenSuccess_thenReturnRequests() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
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

        List<ParticipationRequestDto> requestDtos = requestService.getRequestsByUserId(requester1.getId());

        assertEquals(1, requestDtos.size());
        assertEquals(request.getEvent(), requestDtos.get(0).getEvent());
        assertEquals(request.getRequester(), requestDtos.get(0).getRequester());
        assertEquals(request.getStatus(), requestDtos.get(0).getStatus());
        assertEquals(request.getCreated(), requestDtos.get(0).getCreated());
    }

    @Test
    void getEventRequestsTest_whenSuccess_thenReturnRequests() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
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

        List<ParticipationRequestDto> requestDtos = requestService.getEventRequests(user.getId(), event.getId());

        assertEquals(1, requestDtos.size());
        assertEquals(request.getEvent(), requestDtos.get(0).getEvent());
        assertEquals(request.getRequester(), requestDtos.get(0).getRequester());
        assertEquals(request.getStatus(), requestDtos.get(0).getStatus());
        assertEquals(request.getCreated(), requestDtos.get(0).getCreated());
    }

    @Test
    void changeEventRequestsStatusTest_whenUserIsNotEventInitiator_thenNotFoundException() {
        User user1 = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user1);
        User user2 = User.builder().name("requester1").email("requester1@email.com").build();
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
                .initiator(user1)
                .location(location)
                .paid(false)
                .participantLimit(1)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        assertThrows(NotFoundException.class, () -> requestService.changeEventRequestsStatus(user2.getId(),
                event.getId(), eventRequestStatusUpdateRequest));
    }

    @Test
    void changeEventRequestsStatusTest_whenModerationIsNotRequired_thenRequestException() {
        User user1 = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user1);
        User user2 = User.builder().name("requester1").email("requester1@email.com").build();
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
                .initiator(user1)
                .location(location)
                .paid(false)
                .participantLimit(1)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(1L))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        assertThrows(RequestException.class, () -> requestService.changeEventRequestsStatus(user1.getId(),
                event.getId(), eventRequestStatusUpdateRequest));
    }

    @Test
    void changeEventRequestsStatusTest_whenParticipantLimitIsReached_thenRequestException() {
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
        Request request1 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester2.getId())
                .build();
        entityManager.persist(request2);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(request2.getId()))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        assertThrows(RequestException.class, () -> requestService.changeEventRequestsStatus(user.getId(),
                event.getId(), eventRequestStatusUpdateRequest));
    }

    @Test
    void changeEventRequestsStatusTest_whenParticipantLimitReminderIsLessIdsCount_thenRequestException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
        User requester2 = User.builder().name("requester2").email("requester2@email.com").build();
        entityManager.persist(requester2);
        User requester3 = User.builder().name("requester3").email("requester3@email.com").build();
        entityManager.persist(requester3);
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
                .participantLimit(2)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request1 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester2.getId())
                .build();
        entityManager.persist(request2);
        Request request3 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester3.getId())
                .build();
        entityManager.persist(request3);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(request2.getId(), request3.getId()))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        assertThrows(RequestException.class, () -> requestService.changeEventRequestsStatus(user.getId(),
                event.getId(), eventRequestStatusUpdateRequest));
    }

    @Test
    void changeEventRequestsStatusTest_whenWhenRequestIsNotExists_thenNotFoundException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
        User requester2 = User.builder().name("requester2").email("requester2@email.com").build();
        entityManager.persist(requester2);
        User requester3 = User.builder().name("requester3").email("requester3@email.com").build();
        entityManager.persist(requester3);
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
                .participantLimit(5)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request1 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester2.getId())
                .build();
        entityManager.persist(request2);
        Request request3 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester3.getId())
                .build();
        entityManager.persist(request3);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(request2.getId(), request3.getId(), 999L))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        assertThrows(NotFoundException.class, () -> requestService.changeEventRequestsStatus(user.getId(),
                event.getId(), eventRequestStatusUpdateRequest));
    }

    @Test
    void changeEventRequestsStatusTest_whenWhenRequestIsNotPending_thenNotFoundException() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
        User requester2 = User.builder().name("requester2").email("requester2@email.com").build();
        entityManager.persist(requester2);
        User requester3 = User.builder().name("requester3").email("requester3@email.com").build();
        entityManager.persist(requester3);
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
                .participantLimit(5)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request1 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.REJECTED)
                .event(event.getId())
                .requester(requester2.getId())
                .build();
        entityManager.persist(request2);
        Request request3 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester3.getId())
                .build();
        entityManager.persist(request3);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(request2.getId(), request3.getId()))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        assertThrows(RequestStatusException.class, () -> requestService.changeEventRequestsStatus(user.getId(),
                event.getId(), eventRequestStatusUpdateRequest));
    }

    @Test
    void changeEventRequestsStatusTest_whenWhenLimitIsReached_thenOtherPendingRequestIsRejected() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
        User requester2 = User.builder().name("requester2").email("requester2@email.com").build();
        entityManager.persist(requester2);
        User requester3 = User.builder().name("requester3").email("requester3@email.com").build();
        entityManager.persist(requester3);
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
                .participantLimit(2)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request1 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester2.getId())
                .build();
        entityManager.persist(request2);
        Request request3 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester3.getId())
                .build();
        entityManager.persist(request3);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(request2.getId()))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

         EventRequestStatusUpdateResult result = requestService.changeEventRequestsStatus(user.getId(),
                event.getId(), eventRequestStatusUpdateRequest);

         assertEquals(2, result.getConfirmedRequests().size());
         assertEquals(1, result.getRejectedRequests().size());
         assertEquals(request3.getId(), result.getRejectedRequests().get(0).getId());
    }

    @Test
    void changeEventRequestsStatusTest_whenWhenConfirmed_thenReturnCorrectResult() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User requester1 = User.builder().name("requester1").email("requester1@email.com").build();
        entityManager.persist(requester1);
        User requester2 = User.builder().name("requester2").email("requester2@email.com").build();
        entityManager.persist(requester2);
        User requester3 = User.builder().name("requester3").email("requester3@email.com").build();
        entityManager.persist(requester3);
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
                .participantLimit(3)
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Request request1 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.CONFIRMED)
                .event(event.getId())
                .requester(requester1.getId())
                .build();
        entityManager.persist(request1);
        Request request2 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester2.getId())
                .build();
        entityManager.persist(request2);
        Request request3 = Request.builder()
                .created(LocalDateTime.now().minusDays(1))
                .status(EventRequestStatus.PENDING)
                .event(event.getId())
                .requester(requester3.getId())
                .build();
        entityManager.persist(request3);
        EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest = EventRequestStatusUpdateRequest.builder()
                .requestIds(List.of(request2.getId(), request3.getId()))
                .status(EventRequestUpdateStatus.CONFIRMED)
                .build();

        EventRequestStatusUpdateResult result = requestService.changeEventRequestsStatus(user.getId(),
                event.getId(), eventRequestStatusUpdateRequest);

        assertEquals(3, result.getConfirmedRequests().size());
        assertTrue(result.getRejectedRequests().isEmpty());
    }
}