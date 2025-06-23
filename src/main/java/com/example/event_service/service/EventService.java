package com.example.event_service.service;

import com.example.event_service.dto.EventDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EventService {

    EventDTO createEvent(EventDTO eventDTO, String currentUserEmail);

    EventDTO updateEvent(UUID eventId, EventDTO eventDTO, String currentUserEmail);

    void deleteEvent(UUID eventId, String currentUserEmail);

    EventDTO getEventById(UUID eventId);

    Page<EventDTO> listEvents(String location, String visibility, String dateFrom, String dateTo, Pageable pageable);

    Page<EventDTO> listUpcomingEvents(Pageable pageable);

}
