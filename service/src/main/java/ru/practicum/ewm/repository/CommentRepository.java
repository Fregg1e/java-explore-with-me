package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.user.id = ?1")
    List<Comment> getCommentsByUserId(Long userId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.event.id = ?1")
    List<Comment> getCommentsByEventId(Long eventId, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.event.id = ?1")
    List<Comment> getCommentsByEventIdWithoutPagination(Long eventId);
}
