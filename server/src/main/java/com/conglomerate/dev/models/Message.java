package com.conglomerate.dev.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String content;
    LocalDateTime timestamp;
    int likes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Grouping grouping;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_read_messages_mappings",
            joinColumns=@JoinColumn(name="message_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> read;
}
