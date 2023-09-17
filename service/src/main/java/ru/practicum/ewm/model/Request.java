package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
    @Column(name = "event_id", nullable = false)
    private Long event;
    @Column(name = "requester", nullable = false)
    private Long requester;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EventRequestStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(id, request.id) && Objects.equals(created, request.created)
                && Objects.equals(event, request.event) && Objects.equals(requester, request.requester)
                && status == request.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, created, event, requester, status);
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", created=" + created +
                ", event=" + event +
                ", requester=" + requester +
                ", status=" + status +
                '}';
    }
}
