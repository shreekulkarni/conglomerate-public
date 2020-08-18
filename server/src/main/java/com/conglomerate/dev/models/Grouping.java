package com.conglomerate.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "groupings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grouping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;
    LocalDateTime creationDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_group_mappings",
            joinColumns=@JoinColumn(name="group_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> members;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private User owner;
}
