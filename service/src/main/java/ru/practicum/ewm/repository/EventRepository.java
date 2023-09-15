package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT COUNT(e) FROM Event e WHERE e.category.id = ?1")
    Long getCountEventsByCategoryId(Long catId);
}
