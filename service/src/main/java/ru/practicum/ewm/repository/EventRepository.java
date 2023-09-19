package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT COUNT(e) FROM Event e WHERE e.category.id = ?1")
    Long getCountEventsByCategoryId(Long catId);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1")
    List<Event> getEventsByUserId(Long userId, Pageable pageable);
}
