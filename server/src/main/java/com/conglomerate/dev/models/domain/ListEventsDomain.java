package com.conglomerate.dev.models.domain;

import com.conglomerate.dev.models.Event;
import com.conglomerate.dev.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Value
@Builder
@AllArgsConstructor
public class ListEventsDomain {
    int id;

    String eventName;
    LocalDateTime dateTime;
    int duration;
    boolean recurring;

    Set<User> attendees;

    public static List<ListEventsDomain> listEvents(List<Event> events) {
        List<ListEventsDomain> domains = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            ListEventsDomain current = ListEventsDomain.builder()
                    .eventName(events.get(i).getName())
                    .id(events.get(i).getId())
                    .attendees(events.get(i).getAttendees())
                    .dateTime(events.get(i).getDateTime())
                    .duration(events.get(i).getDuration())
                    .recurring(events.get(i).isRecurring())
                    .build();

            domains.add(current);
        }

        return domains;
    }

    public ListEventsDomain() {
        id = 0;
        eventName = "";
        dateTime = LocalDateTime.now();
        duration = 0;
        attendees = new HashSet<User>();
        recurring = false;
    }
}
