package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.exception.model.AlreadyExistException;
import ru.practicum.ewm.exception.model.NotFoundException;
import ru.practicum.ewm.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final UserServiceImpl userService;

    @Test
    void createUserTest_whenAllDataIsCorrect_returnUserDto() {
        NewUserRequest newUserRequest = NewUserRequest.builder().email("test@test.com").name("test").build();

        UserDto userDto = userService.createUser(newUserRequest);

        assertEquals(newUserRequest.getName(), userDto.getName());
        assertEquals(newUserRequest.getEmail(), userDto.getEmail());
    }

    @Test
    void createUserTest_whenEmailIsExists_throwAlreadyExistsException() {
        User user = User.builder().name("test1").email("test@test.com").build();
        entityManager.persist(user);
        NewUserRequest newUserRequest = NewUserRequest.builder().email("test@test.com").name("test").build();

        assertThrows(AlreadyExistException.class, () -> userService.createUser(newUserRequest));
    }

    @Test
    void deleteUserTest_whenUserIsExists_thenUserWasDelete() {
        User user = User.builder().name("test1").email("test@test.com").build();
        entityManager.persist(user);

        userService.deleteUser(user.getId());

        TypedQuery<Long> query = entityManager
                .createQuery("Select count(u) from User u where u.id = :id", Long.class);
        Long count = query
                .setParameter("id", user.getId())
                .getSingleResult();

        assertEquals(0, count);
    }

    @Test
    void deleteUserTest_whenUserIsNotExists_thenThrowNotFoundException() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(999L));
    }

    @Test
    void getUsersTest_whenIdsIs2And3AndFromIs0AndSizeIs10_thenReturnListUserDto() {
        User user1 = User.builder().name("test1").email("test1@test.com").build();
        entityManager.persist(user1);
        User user2 = User.builder().name("test2").email("test2@test.com").build();
        entityManager.persist(user2);
        User user3 = User.builder().name("test3").email("test3@test.com").build();
        entityManager.persist(user3);

        List<UserDto> userDtos = userService.getUsers(List.of(user2.getId(), user3.getId()), 0, 10);

        assertEquals(2, userDtos.size());
        assertEquals(user2.getId(), userDtos.get(0).getId());
        assertEquals(user3.getId(), userDtos.get(1).getId());
    }

    @Test
    void getUsersTest_whenIdsIsNullAndFromIs1AndSizeIs2_thenReturnListUserDto() {
        User user1 = User.builder().name("test1").email("test1@test.com").build();
        entityManager.persist(user1);
        User user2 = User.builder().name("test2").email("test2@test.com").build();
        entityManager.persist(user2);
        User user3 = User.builder().name("test3").email("test3@test.com").build();
        entityManager.persist(user3);

        List<UserDto> userDtos = userService.getUsers(null, 1, 2);

        assertEquals(2, userDtos.size());
        assertEquals(user2.getId(), userDtos.get(0).getId());
        assertEquals(user3.getId(), userDtos.get(1).getId());
    }
}