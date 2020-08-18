package com.conglomerate.dev.integration.events;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

import java.time.LocalDateTime;

public class TestRSVP {
    @Test
    public void RSVPBadAuth() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("RSVPBadAuth",
                "RSVPBadAuth@email.com",
                "RSVPBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "RSVPBadAuth", 201);

        // CREATE EVENT
        int eventId = TestingUtils.createEventAndExpect(authToken, groupingId, "Bad Auth", LocalDateTime.now(), 1, 201);

        // RSVP
        TestingUtils.RSVPAndExpect("Bad auth", eventId, 400);
    }

    @Test
    public void RSVPInvalidEvent() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("RSVPInvalidEvent",
                "RSVPInvalidEvent@email.com",
                "RSVPInvalidEvent");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "RSVPInvalidEvent", 201);

        // CREATE EVENT
        int eventId = TestingUtils.createEventAndExpect(authToken, groupingId, "RSVPInvalidEvent", LocalDateTime.now(), 1, 201);

        // RSVP
        TestingUtils.RSVPAndExpect(authToken, -1, 400);
    }

    @Test
    public void RSVPNotMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("RSVPNotMember",
                "RSVPNotMember@email.com",
                "RSVPNotMember");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "RSVPNotMember", 201);

        // CREATE EVENT
        int eventId = TestingUtils.createEventAndExpect(authToken, groupingId, "RSVPNotMember", LocalDateTime.now(), 1, 201);

        // CREATE USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("RSVPNotMemberO",
                "RSVPNotMemberO@email.com",
                "RSVPNotMemberO");

        // RSVP
        TestingUtils.RSVPAndExpect(otherToken, eventId, 400);
    }

    @Test
    public void AlreadyRSVPed() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("RSVPTwice",
                "RSVPTwice@email.com",
                "RSVPTwice");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "RSVPTwice", 201);

        // CREATE EVENT
        int eventId = TestingUtils.createEventAndExpect(authToken, groupingId, "RSVPTwice", LocalDateTime.now(), 1, 201);

        // CREATE USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("RSVPTwiceO",
                "RSVPTwiceO@email.com",
                "RSVPTwiceO");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(otherToken, groupingId, 200);

        // RSVP
        TestingUtils.RSVPAndExpect(otherToken, eventId, 200);

        // RSVP AGAIN
        TestingUtils.RSVPAndExpect(otherToken, eventId, 400);
    }

    @Test
    public void RSVPSuccess() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("RSVPSuccess",
                "RSVPSuccess@email.com",
                "RSVPSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "RSVPSuccess", 201);

        // CREATE EVENT
        int eventId = TestingUtils.createEventAndExpect(authToken, groupingId, "RSVPSuccess", LocalDateTime.now(), 1, 201);

        // CREATE USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("RSVPSuccessO",
                "RSVPSuccessO@email.com",
                "RSVPSuccessO");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(otherToken, groupingId, 200);

        // RSVP
        TestingUtils.RSVPAndExpect(otherToken, eventId, 200);
    }
}
