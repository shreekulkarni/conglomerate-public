package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class JoinGroupingDomain {
    private int groupingId;
    private String authToken;

    public JoinGroupingDomain() {
        groupingId = 0;
        authToken = "";
    }
}
