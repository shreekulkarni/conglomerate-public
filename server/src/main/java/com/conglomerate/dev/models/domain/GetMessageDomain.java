package com.conglomerate.dev.models.domain;

import com.conglomerate.dev.models.Message;
import com.conglomerate.dev.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
public class GetMessageDomain  {

    int id;
    String content;
    LocalDateTime timestamp;
    String senderName;
    boolean liked;
    List<String> read;

    public GetMessageDomain() {
        id = 0;
        content = "";
        timestamp = LocalDateTime.now();
        senderName = "Deleted Account";
        liked = false;
        read = new ArrayList<>();
    }

    public static GetMessageDomain fromMessage(Message message) {
        List<String> read = new ArrayList<>();
        for (User u : message.getRead()) {
            read.add(u.getUserName());
        }

        return GetMessageDomain.builder()
                .id(message.getId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .senderName(message.getSender() != null ? message.getSender().getUserName() : "Deleted Account")
                .liked(message.getLikes() > 0)
                .read(read)
                .build();
    }
}
