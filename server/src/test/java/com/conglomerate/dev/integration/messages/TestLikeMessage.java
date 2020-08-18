package com.conglomerate.dev.integration.messages;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.GetMessageDomain;
import org.junit.Test;

import java.util.List;

public class TestLikeMessage {
    @Test
    public void likeMessageSuccess() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("likeMessageSuccess",
                "likeMessageSuccess@email.com",
                "likeMessageSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Like Message Success", 201);

        // SEND MESSAGE
        int messageId = TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // LIKE MESSAGE
        TestingUtils.likeMessageAndExpect(authToken,  messageId, 200);
    }

    @Test
    public void likeMessageIncrements() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("likeMessageIncrements",
                "likeMessageIncrements@email.com",
                "likeMessageIncrements");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Like Message Increments", 201);

        // SEND MESSAGE
        int messageId = TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // ASSERT THE MESSAGE DOES NOT START OUT AS LIKED
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);
        assert(messages.size() == 1);
        assert(!messages.get(0).isLiked());

        // LIKE MESSAGE
        TestingUtils.likeMessageAndExpect(authToken, messageId, 200);

        // ASSERT THE MESSAGE GETS LIKED
        messages = TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).isLiked());
    }

    @Test
    public void likeMessageBadAuth() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("likeMessageBadAuth",
                "likeMessageBadAuth@email.com",
                "likeMessageBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Like Message Bad Auth", 201);

        // SEND MESSAGE
        int messageId = TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // LIKE MESSAGE
        TestingUtils.likeMessageAndExpect("BAD AUTH TOKEN",  messageId, 400);
    }

    @Test
    public void likeMessageNoMessage() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("likeMessageNoMessage",
                "likeMessageNoMessage@email.com",
                "likeMessageNoMessage");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Like Message No Message", 201);

        // SEND MESSAGE
        int messageId = TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // LIKE MESSAGE
        TestingUtils.likeMessageAndExpect(authToken,  -1, 400);
    }

    @Test
    public void likeMessageNotMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("likeMessageNotMember1",
                "likeMessageNotMember1@email.com",
                "likeMessageNotMember1");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Like Message No Message", 201);

        // SEND MESSAGE
        int messageId = TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // CREATE OTHER USER AND LOGIN
        authToken = TestingUtils.createUserAndLoginSuccess("likeMessageNotMember2",
                "likeMessageNotMember2@email.com",
                "likeMessageNotMember2");

        // LIKE MESSAGE
        TestingUtils.likeMessageAndExpect(authToken,  messageId, 400);
    }
}
