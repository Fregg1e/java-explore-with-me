package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT COUNT(e) FROM Event e WHERE e.category.id = ?1")
    Long getCountEventsByCategoryId(Long catId);

    @Query("SELECT e FROM Event e WHERE e.initiator.id = ?1")
    List<Event> getEventsByUserId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE ((e.initiator.id IN ?1) OR ?1 IS NULL) AND ((e.state IN ?2) OR ?2 IS NULL) "
            + "AND ((e.category.id IN ?3) OR ?3 IS NULL) AND ((e.eventDate >= ?4)  OR ?4 IS NULL) "
            + "AND ((e.eventDate <= ?5)  OR ?5 IS NULL)")
    List<Event> getEvents(List<Long> users, List<EventState> states, List<Long> categories,
            LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);
}
