package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.service.UserAdminService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}