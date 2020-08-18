package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class LeaveGroupingDomain {
    private int groupingId;
    private String authToken;

    public LeaveGroupingDomain() {
        groupingId = 0;
        authToken = "";
    }
}
