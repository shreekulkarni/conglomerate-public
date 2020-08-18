package com.conglomerate.dev.models.domain;

import lombok.*;

@Value
@AllArgsConstructor
@Builder
public class SendMessageDomain {
    String content;
    int groupingId;

    public SendMessageDomain() {
        this.content = "";
        this.groupingId = 0;
    }
}
