package com.conglomerate.dev.models.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class UpdateGroupingNameDomain {
    private int groupingId;
    private String newGroupName;

    public UpdateGroupingNameDomain() {
        groupingId = 0;
        newGroupName = "";
    }
}
