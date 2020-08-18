package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Event;
import com.conglomerate.dev.models.domain.CreateEventDomain;
import com.conglomerate.dev.models.domain.ListEventsDomain;
import com.conglomerate.dev.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// the server will look to map requests that start with "/events" to the endpoints in this controller
@RequestMapping(value = "/events")
public class EventController {
    private final EventService eventService;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // @GetMapping maps HTTP GET requests on the endpoint to this method
    // Because no url value has been specified, this is mapping the class-wide "/events" url
    @GetMapping(produces = "application/json; charset=utf-8")
    public List<Event> getAllEvents() {
        // Have the logic in EventService
        // Ideally, EventController should just control the request mappings
        return eventService.getAllEvents();
    }

    @PostMapping(value = "/{groupingId}/create-event")
    @ResponseStatus(HttpStatus.CREATED)
    public int createEvent(@RequestHeader("authorization") String authHeader, @PathVariable int groupingId,
                           @RequestBody CreateEventDomain createEventDomain) {
        String authToken = authHeader.substring(7);

        return eventService.createEvent(authToken, groupingId, createEventDomain);
    }

    @GetMapping(value = "/{groupingId}/list-events", produces = "application/json; charset=utf-8")
    public List<ListEventsDomain> listEvents(@RequestHeader("authorization") String authHeader,
                                             @PathVariable int groupingId) {
        String authToken = authHeader.substring(7);

        return eventService.listGroupingEvents(authToken, groupingId);
    }

    @PostMapping(value = "/{eventId}/RSVP")
    public int RSVP(@RequestHeader("authorization") String authHeader, @PathVariable int eventId) {
        String authToken = authHeader.substring(7);

        return eventService.RSVP(authToken, eventId);
    }
}
