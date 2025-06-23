package com.example.event_service.mapper;

import com.example.event_service.dto.AttendanceDTO;
import com.example.event_service.entity.Attendance;
import com.example.event_service.entity.AttendanceId;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceDTO toDto(Attendance a) {
        if (a == null) return null;
        return AttendanceDTO.builder()
                .eventId(a.getId().getEventId())
                .userId(a.getId().getUserId())
                .status(a.getStatus())
                .respondedAt(a.getRespondedAt())
                .build();
    }

    public Attendance toEntity(AttendanceDTO dto) {
        if (dto == null) return null;
        return Attendance.builder()
                .id(new AttendanceId(dto.getEventId(), dto.getUserId()))
                .status(dto.getStatus())
                .respondedAt(dto.getRespondedAt())
                .build();
    }
}
