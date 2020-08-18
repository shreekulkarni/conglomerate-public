package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class RemoveMemberDomain {
    private int groupingId;
    private String username;

    public RemoveMemberDomain() {
        groupingId = 0;
        username = "";
    }
}
