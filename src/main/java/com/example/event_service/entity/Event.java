package com.example.event_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {
    @Id
    private UUID id;

    private String title;
    private String description;

    @Column(nullable = false)
    private UUID hostId;

    private Instant startTime;
    private Instant endTime;
    private String location;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;

    private Instant createdAt;
    private Instant updatedAt;

    public void setArchived(boolean b) {
    }

    public enum Visibility {
        PUBLIC, PRIVATE
    }
}