package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.service.UserAdminService;
import ru.practicum.ewm.utils.OffsetPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserAdminService {
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = UserMapper.fromNewUserRequestToUser(newUserRequest);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Email должен быть уникальным.",
                    "Пользователь с таким email уже существует!");
        }
        log.debug("Создан пользователь: {}", user);
        return UserMapper.fromUserToUserDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден.",
                String.format("Пользователя с ID = %d не существует.", userId)));
        userRepository.deleteById(user.getId());
        log.debug("Пользователь с id={} удален.", user.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        log.debug("Получение пользователей по параметрам ids={}, from={}, size={}.", ids, from, size);
        return userRepository.getUsers(ids, new OffsetPageRequest(from, size)).stream()
                .map(UserMapper::fromUserToUserDto).collect(Collectors.toList());
    }
}
