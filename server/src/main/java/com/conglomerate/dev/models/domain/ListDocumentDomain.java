package com.conglomerate.dev.models.domain;

import com.conglomerate.dev.models.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class ListDocumentDomain {
    private int id;

    private String name;
    private LocalDateTime uploadDate;
    private String documentLink;

    private String uploaderUsername;

    private int groupingId;
    boolean shared;

    public ListDocumentDomain() {
        id = 0;
        name = "";
        uploadDate = LocalDateTime.now();
        documentLink = "";
        uploaderUsername = "";
        groupingId = 0;
        shared = false;
    }

    public static ListDocumentDomain createDomain(Document document) {
        ListDocumentDomain domain = ListDocumentDomain.builder()
                .id(document.getId())
                .name(document.getName())
                .uploadDate(document.getUploadDate())
                .documentLink(document.getDocumentLink())
                .uploaderUsername(document.getUploader() == null ? "Deleted Account" : document.getUploader().getUserName())
                .groupingId(document.getGrouping().getId())
                .shared(document.isShared())
                .build();

        return domain;
    }

    public static List<ListDocumentDomain> createList(List<Document> documents) {
        List<ListDocumentDomain> list = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            list.add(createDomain(documents.get(i)));
        }

        return list;
    }
}
