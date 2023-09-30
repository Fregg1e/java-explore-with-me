package ru.practicum.ewm.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.model.*;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentPrivateServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final CommentPrivateServiceImpl commentService;

    @Test
    void createCommentTest() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User user2 = User.builder().name("test2").email("test2@email.com").build();
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
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        CommentDto commentDto = CommentDto.builder().text("test123").build();

        CommentDto newCommentDto = commentService.createComment(user2.getId(), event.getId(), commentDto);

        assertEquals(user2.getName(), newCommentDto.getUser());
        assertEquals(commentDto.getText(), newCommentDto.getText());
    }

    @Test
    void updateCommentTest() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User user2 = User.builder().name("test2").email("test2@email.com").build();
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
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Comment comment = Comment.builder().event(event).user(user2)
                .created(LocalDateTime.now()).text("test123").build();
        entityManager.persist(comment);
        CommentDto commentDto = CommentDto.builder().text("test321").build();

        CommentDto updatedCommentDto = commentService.updateComment(user2.getId(), comment.getId(), commentDto);

        assertEquals(commentDto.getText(), updatedCommentDto.getText());
    }

    @Test
    void deleteCommentTest() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User user2 = User.builder().name("test2").email("test2@email.com").build();
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
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Comment comment = Comment.builder().event(event).user(user2)
                .created(LocalDateTime.now()).text("test123").build();
        entityManager.persist(comment);

        commentService.deleteComment(user2.getId(), comment.getId());

        TypedQuery<Long> query = entityManager
                .createQuery("Select count(c) from Comment c where c.id = :id", Long.class);
        Long count = query
                .setParameter("id", comment.getId())
                .getSingleResult();

        assertEquals(0, count);
    }

    @Test
    void getCommentsByUserIdTest() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User user2 = User.builder().name("test2").email("test2@email.com").build();
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
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Comment comment = Comment.builder().event(event).user(user2)
                .created(LocalDateTime.now()).text("test123").build();
        entityManager.persist(comment);

        List<CommentDto> commentDtos = commentService.getCommentsByUserId(user2.getId(), 0, 10);

        assertFalse(commentDtos.isEmpty());
        assertEquals(1, commentDtos.size());
        assertEquals(comment.getText(), commentDtos.get(0).getText());
    }

    @Test
    void getCommentByUserIdAndCommentIdTest() {
        User user = User.builder().name("test").email("test@email.com").build();
        entityManager.persist(user);
        User user2 = User.builder().name("test2").email("test2@email.com").build();
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
                .initiator(user)
                .location(location)
                .paid(false)
                .participantLimit(10)
                .requestModeration(false)
                .state(EventState.PUBLISHED)
                .title("Сплав на байдарках")
                .build();
        entityManager.persist(event);
        Comment comment = Comment.builder().event(event).user(user2)
                .created(LocalDateTime.now()).text("test123").build();
        entityManager.persist(comment);

        CommentDto commentDto = commentService.getCommentByUserIdAndCommentId(user2.getId(), comment.getId());

        assertEquals(comment.getText(), commentDto.getText());
    }
}