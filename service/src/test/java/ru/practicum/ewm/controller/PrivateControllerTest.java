package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.EventRequestStatus;
import ru.practicum.ewm.service.EventPrivateService;
import ru.practicum.ewm.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PrivateController.class)
class PrivateControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private EventPrivateService eventPrivateService;
    @MockBean
    private RequestService requestService;

    @Test
    void createEventTest_whenEventValid_thenStatusIsCreated() throws Exception {
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(1L)
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
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(1L)
                .annotation("Сплав на байдарках похож на полет.")
                .category(CategoryDto.builder().id(1L).name("Сплав").build())
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
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
        when(eventPrivateService.createEvent(any(), any())).thenReturn(eventFullDto);

        mockMvc.perform(post("/users/1/events")
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.annotation", is(newEventDto.getAnnotation())))
                .andExpect(jsonPath("$.title", is(newEventDto.getTitle())));
    }

    @Test
    void createEventTest_whenCategoryIsNull_thenStatusIsBadRequest() throws Exception {
        NewEventDto newEventDto = NewEventDto.builder()
                .annotation("Сплав на байдарках похож на полет.")
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

        mockMvc.perform(post("/users/1/events")
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenAllFieldIsValid_thenStatusIsOk() throws Exception {
        UpdateEventUserRequest newEventDto = UpdateEventUserRequest.builder()
                .annotation("Сплав на байдарках похож на полет.")
                .category(1L)
                .description("Сплав на байдарках похож на полет. На спокойной воде — это парение. "
                        + "На бурной, порожистой — выполнение фигур высшего пилотажа. "
                        + "И то, и другое дарят чувство обновления, феерические эмоции, яркие впечатления.")
                .eventDate(LocalDateTime.now().plusDays(3))
                .location(LocationDto.builder().lat(55.754167F).lon(37.62F).build())
                .requestModeration(false)
                .title("Сплав на байдарках")
                .build();
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(1L)
                .annotation("Сплав на байдарках похож на полет.")
                .category(CategoryDto.builder().id(1L).name("Сплав").build())
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
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
        when(eventPrivateService.updateEventByUserIdAndEventId(any(), any(), any())).thenReturn(eventFullDto);

        mockMvc.perform(patch("/users/1/events/1")
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void updateEventByUserIdAndEventIdTest_whenAllFieldIsNull_thenStatusIsBadRequest() throws Exception {
        UpdateEventUserRequest newEventDto = UpdateEventUserRequest.builder().build();

        mockMvc.perform(patch("/users/1/events/1")
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createRequestTest_whenRequestCreating_thenStatusIsCreated() throws Exception {
        ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder()
                .status(EventRequestStatus.PENDING)
                .created(LocalDateTime.now())
                .requester(1L)
                .event(1L)
                .build();
        when(requestService.createRequest(any(), any())).thenReturn(participationRequestDto);

        mockMvc.perform(post("/users/1/requests")
                        .param("eventId", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void cancelRequestTest_whenCancel_thenStatusIsOk() throws Exception {
        ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder()
                .status(EventRequestStatus.REJECTED)
                .created(LocalDateTime.now())
                .requester(1L)
                .event(1L)
                .build();
        when(requestService.cancelRequest(any(), any())).thenReturn(participationRequestDto);

        mockMvc.perform(patch("/users/1/requests/1/cancel")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getRequestsByUserIdTest_whenSuccess_thenStatusIsOk() throws Exception {
        ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder()
                .status(EventRequestStatus.REJECTED)
                .created(LocalDateTime.now())
                .requester(1L)
                .event(1L)
                .build();
        when(requestService.getRequestsByUserId(any())).thenReturn(List.of(participationRequestDto));

        mockMvc.perform(get("/users/1/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventsByUserIdTest_whenSuccess_thenStatusIsOk() throws Exception {
        when(eventPrivateService.getEventsByUserId(any(), any(), any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users/1/events")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventByUserIdAndEventIdTest_whenSuccess_thenStatusIsOk() throws Exception {
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(1L)
                .annotation("Сплав на байдарках похож на полет.")
                .category(CategoryDto.builder().id(1L).name("Сплав").build())
                .confirmedRequests(0)
                .createdOn(LocalDateTime.now())
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
        when(eventPrivateService.getEventByUserIdAndEventId(any(), any())).thenReturn(eventFullDto);

        mockMvc.perform(get("/users/1/events/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventRequestsTest_whenSuccess_thenStatusIsOk() throws Exception {
        ParticipationRequestDto participationRequestDto = ParticipationRequestDto.builder()
                .status(EventRequestStatus.REJECTED)
                .created(LocalDateTime.now())
                .requester(1L)
                .event(1L)
                .build();
        when(requestService.getEventRequests(any(), any())).thenReturn(List.of(participationRequestDto));

        mockMvc.perform(get("/users/1/events/1/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}