package com.example.event_service.dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID hostId;
    private Instant startTime;
    private Instant endTime;
    private String location;
    private String visibility;
    private Instant createdAt;
    private Instant updatedAt;
}