package com.conglomerate.dev.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "folders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Folder  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Grouping grouping;

    @OneToMany(mappedBy = "folder")
    List<Document> documents;
}
