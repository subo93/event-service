package com.example.event_service.mapper;

import com.example.event_service.dto.EventDTO;
import com.example.event_service.entity.Event;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventDTO toDto(Event event);
    Event toEntity(EventDTO dto);
}