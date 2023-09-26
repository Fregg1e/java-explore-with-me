package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.service.CategoryPublicService;
import ru.practicum.ewm.service.CompilationPublicService;
import ru.practicum.ewm.service.EventPublicService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicController.class)
class PublicControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompilationPublicService compilationPublicService;
    @MockBean
    private CategoryPublicService categoryPublicService;
    @MockBean
    private EventPublicService eventPublicService;

    @Test
    void getCompilationsTest_whenSuccess_thenStatusIsOk() throws Exception {
        CompilationDto compilationDto = CompilationDto.builder().title("title").pinned(true).build();
        when(compilationPublicService.getCompilations(any(), any(), any())).thenReturn(List.of(compilationDto));

        mockMvc.perform(get("/compilations")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCompilationByIdTest_whenSuccess_thenStatusIsOk() throws Exception {
        CompilationDto compilationDto = CompilationDto.builder().title("title").pinned(true).build();
        when(compilationPublicService.getCompilationById(any())).thenReturn(compilationDto);

        mockMvc.perform(get("/compilations/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoriesTest_whenSuccess_thenStatusIsOk() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("test").build();
        when(categoryPublicService.getCategories(any(), any())).thenReturn(List.of(categoryDto));

        mockMvc.perform(get("/categories")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getCategoryByIdTest_whenSuccess_thenStatusIsOk() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("test").build();
        when(categoryPublicService.getCategoryById(any())).thenReturn(categoryDto);

        mockMvc.perform(get("/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventsTest_whenSuccess_thenStatusIsOk() throws Exception {
        EventShortDto eventShortDto = EventShortDto.builder()
                .annotation("test")
                .category(CategoryDto.builder().id(1L).name("test").build())
                .eventDate(LocalDateTime.now())
                .id(1L)
                .initiator(UserShortDto.builder().id(1L).name("test").build())
                .paid(true)
                .title("test")
                .build();
        when(eventPublicService.getEvents(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(eventShortDto));

        mockMvc.perform(get("/events")
                        .param("text", "test")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventByIdTest_whenSuccess_thenStatusIsOk() throws Exception {
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
        when(eventPublicService.getEventById(any(), any())).thenReturn(eventFullDto);

        mockMvc.perform(get("/events/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}