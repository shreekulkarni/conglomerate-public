package com.conglomerate.dev.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;
    LocalDateTime uploadDate;
    String documentLink;
    boolean shared;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id")
    private User uploader;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Grouping grouping;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Folder folder;

    /*
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_document_mappings",
            joinColumns=@JoinColumn(name="document_id"),
            inverseJoinColumns=@JoinColumn(name="user_id"))
    private Set<User> viewPermissions;
    */
}
