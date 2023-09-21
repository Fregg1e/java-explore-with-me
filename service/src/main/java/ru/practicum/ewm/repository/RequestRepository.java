package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.EventRequestStatus;
import ru.practicum.ewm.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query("SELECT r FROM Request r WHERE r.requester = ?1 AND r.event = ?2")
    Request getRequestByUserIdAndEventId(Long userId, Long eventId);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.event = ?1 AND r.status = 'CONFIRMED'")
    Integer getCountApprovedRequestsByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.requester = ?1")
    List<Request> getRequestsByUserId(Long userId);

    @Query("SELECT r FROM Request r WHERE r.event = ?1")
    List<Request> getRequestsByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.id IN ?1")
    List<Request> getRequestsByRequestIds(List<Long> requestIds);

    @Query("SELECT r FROM Request r WHERE r.event = ?1 AND r.status = ?2")
    List<Request> getRequestsByEventIdAndStatus(Long eventId, EventRequestStatus status);
}
