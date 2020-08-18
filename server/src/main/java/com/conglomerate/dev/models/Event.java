package com.conglomerate.dev.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;
    String location;

    LocalDateTime dateTime;
    int duration;

    boolean recurring;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Grouping grouping;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_event_mappings",
            joinColumns=@JoinColumn(name="event_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> attendees;
}
