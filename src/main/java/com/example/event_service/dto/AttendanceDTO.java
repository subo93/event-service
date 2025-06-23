package com.example.event_service.dto;

import com.example.event_service.entity.Attendance.Status;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class AttendanceDTO {
    private UUID eventId;
    private UUID userId;
    private Status status;
    private Instant respondedAt;
}
