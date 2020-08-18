package com.conglomerate.dev.models.domain;

import com.conglomerate.dev.models.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class ListFolderDomain {
    private int id;
    private String name;
    private int groupingId;
    private List<ListDocumentDomain> documents;

    public ListFolderDomain() {
        id = 0;
        name = "";
        groupingId = 0;
        documents = new ArrayList<>();
    }
}
