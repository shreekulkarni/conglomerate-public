package com.conglomerate.dev.services;

import com.conglomerate.dev.Exceptions.*;
import com.conglomerate.dev.models.Event;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.CreateEventDomain;
import com.conglomerate.dev.models.domain.ListEventsDomain;
import com.conglomerate.dev.repositories.*;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final GroupingRepository groupingRepository;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public EventService(EventRepository eventRepository, UserRepository userRepository,
                        GroupingRepository groupingRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.groupingRepository = groupingRepository;
    }

    public List<Event> getAllEvents() {
        // this is the logic for the controller endpoint -- it's a simple service so there isn't much logic
        return eventRepository.findAll();
    }

    public int createEvent(String authToken, int groupingId, CreateEventDomain createEventDomain) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Grouping> maybeGrouping = groupingRepository.findById(groupingId);
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(groupingId);
        }
        Grouping grouping = maybeGrouping.get();

        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), groupingId);
        }

        Event event = Event.builder()
                .grouping(grouping)
                .name(createEventDomain.getEventName())
                .dateTime(createEventDomain.getDateTime())
                .duration(createEventDomain.getDuration())
                .recurring(createEventDomain.isRecurring())
                .location("")
                .build();

        event.setAttendees(new HashSet<>());

        event.getAttendees().add(user);

        eventRepository.save(event);

        return event.getId();
    }

    public List<ListEventsDomain> listGroupingEvents(String authToken, int groupingId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Grouping> maybeGrouping = groupingRepository.findById(groupingId);
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(groupingId);
        }
        Grouping grouping = maybeGrouping.get();

        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), groupingId);
        }

        List<Event> events = eventRepository.findByGrouping(grouping);
        List<ListEventsDomain> domains = ListEventsDomain.listEvents(events);

        return domains;
    }

    // runs every 5 minutes
    @Scheduled(fixedRate=300000)
    public void clearOldEvents() {
        LocalDateTime now = LocalDateTime.now();

        int cleared = 0;
        int refreshed = 0;

        for (Event e : eventRepository.findByDateTimeBefore(now)) {
            // check to make sure the event is OVER not just started
            if ((e.getDateTime().plusHours(e.getDuration())).isBefore(now)) {
                if (e.isRecurring()) {
                    e.setDateTime(e.getDateTime().plusDays(7));
                    refreshed++;
                } else {
                    eventRepository.delete(e);
                    cleared++;
                }
            }
        }

        System.out.println(now + " " + "" +
                "[clearOldEvents]: Refreshed " + refreshed + " recurring events and cleared " + cleared + " past events");
    }


    public int RSVP(String authToken, int eventId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Event> maybeEvent = eventRepository.findById(eventId);
        if (!maybeEvent.isPresent()) {
            throw new NoSuchEventException(eventId);
        }
        Event event = maybeEvent.get();

        Grouping grouping = event.getGrouping();
        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), grouping.getId());
        }

        if (event.getAttendees().contains(user)) {
            throw new AlreadyRSVPedException(user.getUserName());
        }

        event.getAttendees().add(user);
        eventRepository.save(event);

        return event.getId();
    }

}
