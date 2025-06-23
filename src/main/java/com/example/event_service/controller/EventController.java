package com.example.event_service.controller;

import com.example.event_service.dto.EventDTO;
import com.example.event_service.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // Create event - ADMIN only
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventDTO eventDTO,
                                                Authentication authentication) {
        String email = authentication.getName();
        EventDTO created = eventService.createEvent(eventDTO, email);
        return ResponseEntity.ok(created);
    }

    // Update event - host or admin
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable UUID id,
                                                @Valid @RequestBody EventDTO eventDTO,
                                                Authentication authentication) {
        String email = authentication.getName();
        EventDTO updated = eventService.updateEvent(id, eventDTO, email);
        return ResponseEntity.ok(updated);
    }

    // Delete event - host or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID id,
                                            Authentication authentication) {
        String email = authentication.getName();
        eventService.deleteEvent(id, email);
        return ResponseEntity.noContent().build();
    }

    // Get event details by id
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID id) {
        EventDTO dto = eventService.getEventById(id);
        return ResponseEntity.ok(dto);
    }

    // List events with optional filters
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EventDTO>> listEvents(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String visibility,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        Page<EventDTO> pageResult = eventService.listEvents(location, visibility, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(pageResult);
    }

    // List upcoming events (paginated)
    @GetMapping("/upcoming")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<EventDTO>> upcomingEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").ascending());
        Page<EventDTO> pageResult = eventService.listUpcomingEvents(pageable);
        return ResponseEntity.ok(pageResult);
    }
}
