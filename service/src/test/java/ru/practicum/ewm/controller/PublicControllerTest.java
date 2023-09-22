package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationPublicService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PublicController.class)
class PublicControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompilationPublicService compilationPublicService;

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
}