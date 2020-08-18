package com.conglomerate.dev.integration.messages;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestSendMessage {
    @Test
    public void sendMessageSuccess() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("sendMessageSuccess",
                "sendMessageSuccess@email.com",
                "sendMessageSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

    }

    @Test
    public void sendMessageUserNotMember() throws Exception {

        // CREATE USER AND LOGIN
        String ownerToken = TestingUtils.createUserAndLoginSuccess("sendMessageNotMember",
                "sendMessageNotMember@email.com",
                "sendMessageNotMember");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(ownerToken, "New Group", 201);

        // CREATE USER (NOT A MEMBER)
        String authToken = TestingUtils.createUserAndLoginSuccess("sendMessageNotMemberDiff",
                "sendMessageNotMemberDiff@email.com",
        "sendMessageNotMemberDiff");

        // SEND MESSAGE (EXPECT FAIL)
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 400);

    }

    @Test
    public void sendMessageNoSuchGrouping() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("sendMessageNoGrouping",
                "sendMessageNoGrouping@email.com",
                "sendMessageNoGrouping");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND MESSAGE (WRONG GROUP, EXPECT FAIL)
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", -1, 400);

    }
}
