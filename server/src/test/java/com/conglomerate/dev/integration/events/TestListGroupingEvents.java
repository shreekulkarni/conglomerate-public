package com.conglomerate.dev.integration.events;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.ListEventsDomain;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

public class TestListGroupingEvents {
    @Test
    public void badAuthToken() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listEventBadAuth",
                "listEventBadAuth@email.com",
                "listEventBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listEventBadAuth", 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, groupingId, "Bad Auth", LocalDateTime.now(), 1, 201);

        // LIST EVENTS
        TestingUtils.listGroupEventsAndExpect("Bad auth", groupingId, 400);
    }

    @Test
    public void noGrouping() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listEventNoG",
                "listEventNoG@email.com",
                "listEventNoG");

        // LIST EVENTS
        TestingUtils.listGroupEventsAndExpect(authToken, -1, 400);
    }

    @Test
    public void notMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listEventNotMember",
                "listEventNotMember@email.com",
                "listEventNotMember");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listEventNotMember", 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, groupingId, "listEventNotMember", LocalDateTime.now(), 1, 201);

        // CREATE USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("listEventNotMemberO",
                "listEventNotMemberO@email.com",
                "listEventNotMemberO");

        // LIST EVENTS
        TestingUtils.listGroupEventsAndExpect(otherToken, groupingId, 400);
    }

    @Test
    public void noEvents() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listEventNoEvents",
                "listEventNoEvents@email.com",
                "listEventNoEvents");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listEventNoEvents", 201);

        // LIST EVENTS
        List<ListEventsDomain> events = TestingUtils.listGroupEventsAndExpect(authToken, groupingId, 200);

        assert(events.size() == 0);
    }

    @Test
    public void oneEvent() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listEventOneEvent5",
                "listEventOneEvent5@email.com",
                "listEventOneEvent5");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listEventOneEvent", 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, groupingId, "listEventOneEvent", LocalDateTime.now(), 1, 201);

        // LIST EVENTS
        List<ListEventsDomain> events = TestingUtils.listGroupEventsAndExpect(authToken, groupingId, 200);

        assert(events.size() == 1);
        System.out.println(events);
    }

    @Test
    public void multipleEvents() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listEventTwoEvents",
                "listEventTwoEvents@email.com",
                "listEventTwoEvents");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listEventTwoEvents", 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, groupingId, "listEventTwoEvents", LocalDateTime.now(), 1, 201);

        // CREATE EVENT
        TestingUtils.createEventAndExpect(authToken, groupingId, "listEventTwoEvents1", LocalDateTime.now(), 1, 201);

        // LIST EVENTS
        List<ListEventsDomain> events = TestingUtils.listGroupEventsAndExpect(authToken, groupingId, 200);

        assert(events.size() == 2);
        System.out.println(events);
    }
}
