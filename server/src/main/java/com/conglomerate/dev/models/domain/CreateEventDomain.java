package com.conglomerate.dev.models.domain;

import com.amazonaws.services.dynamodbv2.xspec.L;
import com.conglomerate.dev.models.Event;
import com.conglomerate.dev.models.User;
import com.google.api.services.storage.Storage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor
public class CreateEventDomain {
    String eventName;
    LocalDateTime dateTime;
    int duration;
    boolean recurring;

    public CreateEventDomain() {
        eventName = "";
        dateTime = LocalDateTime.now();
        duration = 0;
        recurring = false;
    }
}
