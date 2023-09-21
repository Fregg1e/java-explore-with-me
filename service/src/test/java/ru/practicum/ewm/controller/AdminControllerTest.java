package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.service.CategoryAdminService;
import ru.practicum.ewm.service.EventAdminService;
import ru.practicum.ewm.service.UserAdminService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminController.class)
class AdminControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserAdminService userAdminService;
    @MockBean
    private CategoryAdminService categoryAdminService;
    @MockBean
    private EventAdminService eventAdminService;

    @Test
    void createUserTest_whenUserCorrect_thenReturnNewUser() throws Exception {
        NewUserRequest newUserRequest = NewUserRequest.builder().email("test@test.com").name("test").build();
        UserDto userDto = UserDto.builder().id(1L).email("test@test.com").name("test").build();
        when(userAdminService.createUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(newUserRequest.getName())))
                .andExpect(jsonPath("$.email", is(newUserRequest.getEmail())));
    }

    @Test
    void createUserTest_whenEmailIncorrect_thenBadRequest() throws Exception {
        NewUserRequest newUserRequest = NewUserRequest.builder().email("test").name("test").build();

        mockMvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserTest_whenEmailIsExists_thenConflict() throws Exception {
        NewUserRequest newUserRequest = NewUserRequest.builder().email("test@test.com").name("test").build();
        when(userAdminService.createUser(any())).thenThrow(new AlreadyExistException("testM", "testR"));

        mockMvc.perform(post("/admin/users")
                        .content(mapper.writeValueAsString(newUserRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("testM")))
                .andExpect(jsonPath("$.reason", is("testR")));
    }

    @Test
    void deleteUserTest_whenUserExists_thenStatusIsNoContent() throws Exception {
        mockMvc.perform(delete("/admin/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUserTest_whenUserNotExists_thenStatusIsNotFound() throws Exception {
        doThrow(new NotFoundException("Пользователь не найден.", "Пользователя с ID = 1 не существует."))
                .when(userAdminService).deleteUser(1L);

        mockMvc.perform(delete("/admin/users/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Пользователь не найден.")))
                .andExpect(jsonPath("$.reason", is("Пользователя с ID = 1 не существует.")));
    }

    @Test
    void getUsersTest_whenIdsSizeIs1AndFrom0AndSize1_thenStatusIsOk() throws Exception {
        UserDto userDto = UserDto.builder().id(1L).email("test@test.com").name("test").build();
        when(userAdminService.getUsers(any(), any(), any())).thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                .param("ids", "1")
                .param("from", "0")
                .param("size", "1")
                .characterEncoding(StandardCharsets.UTF_8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void getUsersTest_whenIdsSizeIsNullAndFrom0AndSize1_thenStatusIsOk() throws Exception {
        UserDto userDto = UserDto.builder().id(1L).email("test@test.com").name("test").build();
        when(userAdminService.getUsers(any(), any(), any())).thenReturn(List.of(userDto));

        mockMvc.perform(get("/admin/users")
                        .param("from", "0")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }

    @Test
    void getUsersTest_whenFromIsLessThanZero_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .param("from", "-1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUsersTest_whenSizeIsZero_thenStatusIsBadRequest() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .param("from", "0")
                        .param("size", "0")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createCategoryTest_whenCategoryNameIsCorrect_thenStatusIsCreated() throws Exception {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("test").build();
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("test").build();
        when(categoryAdminService.createCategory(any())).thenReturn(categoryDto);

        mockMvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void createCategoryTest_whenCategoryNameIsExists_thenStatusIsConflict() throws Exception {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("test").build();
        when(categoryAdminService.createCategory(any())).thenThrow(AlreadyExistException.class);

        mockMvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void createCategoryTest_whenCategoryNameIsBlank_thenStatusIsBadRequest() throws Exception {
        NewCategoryDto newCategoryDto = NewCategoryDto.builder().name("").build();

        mockMvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCategoryTest_whenCategoryIsExist_thenStatusIsNoContent() throws Exception {
        mockMvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategoryTest_whenCategoryIsNotExist_thenStatusIsNotFound() throws Exception {
        doThrow(new NotFoundException("Категория не найдена.", "Категории с ID = 1 не существует."))
                .when(categoryAdminService).deleteCategory(1L);

        mockMvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Категория не найдена.")))
                .andExpect(jsonPath("$.reason", is("Категории с ID = 1 не существует.")));
    }

    @Test
    void deleteCategoryTest_whenCategoryIsUsed_thenStatusIsConflict() throws Exception {
        doThrow(new AlreadyExistException("Категория не пуста.", "В категории с ID = 1 существует event."))
                .when(categoryAdminService).deleteCategory(1L);

        mockMvc.perform(delete("/admin/categories/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Категория не пуста.")))
                .andExpect(jsonPath("$.reason", is("В категории с ID = 1 существует event.")));
    }

    @Test
    void updateCategoryTest_whenCategoryIsExists_thenStatusIsOK() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("test").build();
        when(categoryAdminService.updateCategory(any(), any())).thenReturn(categoryDto);

        mockMvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is(categoryDto.getName())));
    }

    @Test
    void updateCategoryTest_whenCategoryIsNotExists_thenStatusIsNotFound() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("test").build();
        when(categoryAdminService.updateCategory(any(), any())).thenThrow(NotFoundException.class);

        mockMvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategoryTest_whenCategoryNameIsAlreadyExists_thenStatusIsConflict() throws Exception {
        CategoryDto categoryDto = CategoryDto.builder().id(1L).name("test").build();
        when(categoryAdminService.updateCategory(any(), any())).thenThrow(AlreadyExistException.class);

        mockMvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(categoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateEventTest_whenUpdate_thenStatusIsOk() throws Exception {
        UpdateEventAdminRequest newEventDto = UpdateEventAdminRequest.builder()
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
        when(eventAdminService.updateEventAdmin(any(), any())).thenReturn(eventFullDto);

        mockMvc.perform(patch("/admin/events/1")
                        .content(mapper.writeValueAsString(newEventDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void getEventsTest_whenRequestIsValid_thenStatusIsOk() throws Exception {
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
        when(eventAdminService.getEventsAdmin(any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(eventFullDto));

        mockMvc.perform(get("/admin/events")
                        .param("users", "1")
                        .param("states", "PENDING")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}