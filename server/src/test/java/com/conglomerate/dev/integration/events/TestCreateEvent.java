package com.conglomerate.dev.integration.events;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

import java.time.LocalDateTime;

public class TestCreateEvent {
    @Test
    public void badAuthToken() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createEventBadAuth",
                "createEventBadAuth@email.com",
                "createEventBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "createEventBadAuth", 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect("Bad Auth", groupingId, "Bad Auth", LocalDateTime.now(), 1, 400);
    }

    @Test
    public void noGrouping() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createEventNoG",
                "createEventNoG@email.com",
                "createEventNoG");

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, -1, "createEventNoG", LocalDateTime.now(), 2,400);
    }

    @Test
    public void notMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createEventNotMember",
                "createEventNotMember@email.com",
                "createEventNotMember");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "createEventNotMember", 201);

        // CREATE USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("createEventNotMemberO",
                "createEventNotMemberO@email.com",
                "createEventNotMemberO");

        // CREATE EVENT
        TestingUtils.createEventAndExpect(otherToken, groupingId, "createEventNotMemberO", LocalDateTime.now(), 1, 400);
    }

    @Test
    public void success() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createEventSuccess",
                "createEventSuccess@email.com",
                "createEventSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "createEventSuccess", 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, groupingId, "createEventNotMemberO", LocalDateTime.now(), 1, 201);
    }
}
