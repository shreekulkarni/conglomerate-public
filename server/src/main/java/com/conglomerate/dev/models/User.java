package com.conglomerate.dev.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String email;
    String passwordHash;
    String calendarLink;
    String userName;

    @JsonIgnore
    String profilePic;

    String authTokenHash;
    String googleIdToken;
    String googleRefreshToken;

    @OneToMany(mappedBy = "user")
    private List<Device> devices;
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "user_group_mappings",
//            joinColumns=@JoinColumn(name="user_id"),
//            inverseJoinColumns=@JoinColumn(name="group_id"))
//    private Set<Grouping> groupingSet;
}
