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
@Table(name = "endpoint_hit")
public class EndpointHit {
    @Id
    @Column(name = "endpoint_hit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "endpoint_hit_timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointHit that = (EndpointHit) o;
        return Objects.equals(id, that.id) && Objects.equals(app, that.app) && Objects.equals(
                uri, that.uri) && Objects.equals(ip, that.ip) && Objects.equals(timestamp,
                that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, app, uri, ip, timestamp);
    }

    @Override
    public String toString() {
        return "EndpointHit{" +
                "id=" + id +
                ", app='" + app + '\'' +
                ", uri='" + uri + '\'' +
                ", ip='" + ip + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
