package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class ListGroupDocsDomain {
    private List<ListDocumentDomain> documents;
    private List<ListFolderDomain> folders;

    public ListGroupDocsDomain() {
        documents = new ArrayList<ListDocumentDomain>();
        folders = new ArrayList<ListFolderDomain>();
    }
}
