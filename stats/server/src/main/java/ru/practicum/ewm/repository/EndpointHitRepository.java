package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {
    @Query("SELECT new ru.practicum.ewm.model.ViewStats(eh.app, eh.uri, COUNT(*)) "
            + "FROM EndpointHit eh "
            + "WHERE (eh.timestamp BETWEEN ?1 AND ?2) AND ((eh.uri IN ?3) OR ?3 IS NULL) "
            + "GROUP BY eh.app, eh.uri "
            + "ORDER BY COUNT(*) DESC")
    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.ewm.model.ViewStats(eh.app, eh.uri, COUNT(DISTINCT eh.ip)) "
            + "FROM EndpointHit eh "
            + "WHERE (eh.timestamp BETWEEN ?1 AND ?2) AND ((eh.uri IN ?3) OR ?3 IS NULL) "
            + "GROUP BY eh.app, eh.uri "
            + "ORDER BY COUNT(DISTINCT eh.ip) DESC")
    List<ViewStats> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
