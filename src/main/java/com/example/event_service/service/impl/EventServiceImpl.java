package com.example.event_service.service.impl;

import com.example.event_service.enums.UserRole;
import com.example.event_service.dto.EventDTO;
import com.example.event_service.entity.Event;
import com.example.event_service.entity.User;
import com.example.event_service.exception.ResourceNotFoundException;
import com.example.event_service.repository.EventRepository;
import com.example.event_service.repository.UserRepository;
import com.example.event_service.service.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public EventDTO createEvent(EventDTO eventDTO, String currentUserEmail) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only admins can create events");
        }

        Event event = new Event();
        event.setId(UUID.randomUUID());
        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setLocation(eventDTO.getLocation());
        event.setVisibility(Event.Visibility.valueOf(eventDTO.getVisibility()));
        event.setHostId(user.getId());
        event.setCreatedAt(Instant.now());
        event.setUpdatedAt(Instant.now());

        Event saved = eventRepository.save(event);

        EventDTO responseDto = EventDTO.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .description(saved.getDescription())
                .hostId(saved.getHostId())
                .startTime(saved.getStartTime())
                .endTime(saved.getEndTime())
                .location(saved.getLocation())
                .visibility(saved.getVisibility() != null ? saved.getVisibility().name() : null)
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();

        return responseDto;
    }

    @Override
    public EventDTO updateEvent(UUID eventId, EventDTO eventDTO, String currentUserEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isHost = event.getHostId().equals(user.getId());

        if (!isAdmin && !isHost) {
            throw new AccessDeniedException("Only admin or host can update event");
        }

        event.setTitle(eventDTO.getTitle());
        event.setDescription(eventDTO.getDescription());
        event.setStartTime(eventDTO.getStartTime());
        event.setEndTime(eventDTO.getEndTime());
        event.setLocation(eventDTO.getLocation());

        if (eventDTO.getVisibility() != null) {
            try {
                Event.Visibility visibility = Event.Visibility.valueOf(eventDTO.getVisibility());
                event.setVisibility(visibility);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid visibility value");
            }
        }

        event.setUpdatedAt(Instant.now());

        Event updated = eventRepository.save(event);
        return EventDTO.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .description(updated.getDescription())
                .hostId(updated.getHostId())
                .startTime(updated.getStartTime())
                .endTime(updated.getEndTime())
                .location(updated.getLocation())
                .visibility(updated.getVisibility() != null ? updated.getVisibility().name() : null)
                .createdAt(updated.getCreatedAt())
                .updatedAt(updated.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteEvent(UUID eventId, String currentUserEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = user.getRole() == UserRole.ADMIN;
        boolean isHost = event.getHostId().equals(user.getId());

        if (!isAdmin && !isHost) {
            throw new AccessDeniedException("Only admin or host can delete event");
        }

        eventRepository.delete(event);
    }

    @Override
    public EventDTO getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .hostId(event.getHostId())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .visibility(event.getVisibility() != null ? event.getVisibility().name() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    @Override
    public Page<EventDTO> listEvents(String location, String visibility, String dateFrom, String dateTo, Pageable pageable) {
        Specification<Event> spec = (root, query, cb) -> cb.conjunction();

        if (location != null && !location.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("location"), location));
        }

        if (visibility != null && !visibility.isEmpty()) {
            try {
                Event.Visibility vis = Event.Visibility.valueOf(visibility);
                spec = spec.and((root, query, cb) -> cb.equal(root.get("visibility"), vis));
            } catch (IllegalArgumentException ignored) {}
        }

        if (dateFrom != null && !dateFrom.isEmpty()) {
            LocalDate from = LocalDate.parse(dateFrom);
            Instant fromInstant = from.atStartOfDay().toInstant(ZoneOffset.UTC);
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startTime"), fromInstant));
        }

        if (dateTo != null && !dateTo.isEmpty()) {
            LocalDate to = LocalDate.parse(dateTo);
            Instant toInstant = to.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("endTime"), toInstant));
        }

        Page<Event> events = eventRepository.findAll(spec, pageable);
        return events.map(event -> EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .hostId(event.getHostId())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .visibility(event.getVisibility() != null ? event.getVisibility().name() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build());
    }

    @Override
    public Page<EventDTO> listUpcomingEvents(Pageable pageable) {
        Instant now = Instant.now();
        Specification<Event> spec = (root, query, cb) -> cb.greaterThan(root.get("startTime"), now);
        Page<Event> events = eventRepository.findAll(spec, pageable);
        return events.map(event -> EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .hostId(event.getHostId())
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .visibility(event.getVisibility() != null ? event.getVisibility().name() : null)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build());
    }
}