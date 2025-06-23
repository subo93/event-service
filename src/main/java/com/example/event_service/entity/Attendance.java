package com.example.event_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "attendance")
public class Attendance {
    @EmbeddedId
    private AttendanceId id;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant respondedAt;

    public enum Status {
        GOING, MAYBE, DECLINED
    }
}