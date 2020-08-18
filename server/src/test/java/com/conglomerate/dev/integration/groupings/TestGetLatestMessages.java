package com.conglomerate.dev.integration.groupings;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.GetMessageDomain;
import org.junit.Test;

import java.util.List;

public class TestGetLatestMessages {
    @Test
    public void getLatestMessagesOne() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesSuccess",
                "getLatestMessagesSuccess@email.com",
                "getLatestMessagesSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // GET LATEST MESSAGE
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        assert(messages.size() == 1);
    }

    @Test
    public void getLatestMessagesThirty() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesThirty",
                "getLatestMessagesThirty@email.com",
                "getLatestMessagesThirty");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND THIRTY MESSAGES
        for (int i = 0; i < 30; i++)
            TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // GET LATEST MESSAGES
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        // SHOULD RETURN 30
        assert(messages.size() == 30);
    }

    @Test
    public void getLatestMessagesThirtyOne() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesThirtyOne",
                "getLatestMessagesThirtyOne@email.com",
                "getLatestMessagesThirtyOne");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND THIRTY MESSAGES
        for (int i = 0; i < 31; i++)
            TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // GET LATEST MESSAGES
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        // SHOULD STILL ONLY RETURN 30
        assert(messages.size() == 30);
    }

    @Test
    public void getLatestMessagesSorted() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesSorted",
                "getLatestMessagesSorted@email.com",
                "getLatestMessagesSorted");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND THIRTY MESSAGES
        for (int i = 0; i < 30; i++) {
            TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);
            Thread.sleep(100);
        }


        // GET LATEST MESSAGES
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        // SHOULD BE SORTED, WITH LATEST FIRST
        for (int i = 0; i < messages.size() - 1; i++)
            assert(messages.get(0).getTimestamp().compareTo(messages.get(1).getTimestamp()) >= 0);
    }

    @Test
    public void getLatestMessagesNotMember() throws Exception {

        // CREATE USER AND LOGIN
        String ownerToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesNotMember",
                "getLatestMessagesNotMember@email.com",
                "getLatestMessagesNotMember");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(ownerToken, "New Group", 201);

        // CREATE OTHER USER
        String authToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesOther",
                "getLatestMessagesOther@email.com",
                "getLatestMessagesOther");

        // GET LATEST MESSAGE
        TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 400);

    }

    @Test
    public void getLatestMessagesNoGroup() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getLatestMessagesNoGroup",
                "getLatestMessagesNoGroup@email.com",
                "getLatestMessagesNoGroup");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // GET LATEST MESSAGE
        TestingUtils.getLatestMessagesAndExpect(authToken, -1, 400);

    }
}
