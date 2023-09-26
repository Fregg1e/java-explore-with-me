package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserAdminService {
    UserDto createUser(NewUserRequest newUserRequest);

    void  deleteUser(Long userId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);
}
