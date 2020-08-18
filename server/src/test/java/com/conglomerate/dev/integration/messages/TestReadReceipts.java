package com.conglomerate.dev.integration.messages;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.GetMessageDomain;
import org.junit.Test;

import java.util.List;

public class TestReadReceipts {

    @Test
    public void noneRead() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("noneRead",
                "noneRead@email.com",
                "noneRead");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "None Read Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // GET LATEST MESSAGE
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).getRead().size() == 0);

    }

    @Test
    public void selfRead() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("selfRead",
                "selfRead@email.com",
                "selfRead");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Self Read Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // GET LATEST MESSAGE
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).getRead().size() == 0);

        // GET MESSAGE AGAIN, SHOULD HAVE BEEN MARKED AS "READ"
        messages = TestingUtils.getLatestMessagesAndExpect(authToken,groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).getRead().size() == 1);
        assert(messages.get(0).getRead().get(0).equals("selfRead"));

    }

    @Test
    public void readByOneOther() throws Exception {

        // CREATE SENDER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("readByOneOther1",
                "readByOneOther1@email.com",
                "readByOneOther1");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "readByOneOther Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // CREATE READER AND LOGIN
        String readerToken = TestingUtils.createUserAndLoginSuccess("readByOneOther2",
                "readByOneOther2@email.com",
                "readByOneOther2");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(readerToken, groupingId, 200);

        // READER GETS LATEST MESSAGE
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(readerToken, groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).getRead().size() == 0);

        // SENDER GETS MESSAGE, SHOULD HAVE READER IN "READ"
        messages = TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).getRead().size() == 1);
        assert(messages.get(0).getRead().get(0).equals("readByOneOther2"));

    }

    @Test
    public void readByManyOthers() throws Exception {
        // CREATE SENDER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("readByManyOthers",
                "readByManyOthers@email.com",
                "readByManyOthers");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "readByManyOthers Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test Message", groupingId, 201);

        // CREATE 5 READERS IN THE GROUP
        for (int i = 0; i < 5; i++) {
            String readerToken = TestingUtils.createUserAndLoginSuccess("readByManyOthers" + i,
                    "readByManyOthers" + i + "@email.com",
                    "readByManyOthers" + i);

            // JOIN GROUP
            TestingUtils.joinGroupingAndExpect(readerToken, groupingId, 200);

            // READERS GETS LATEST MESSAGE
            List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(readerToken, groupingId, 200);
            assert (messages.size() == 1);
        }

        // SENDER GETS MESSAGE, SHOULD HAVE READER IN "READ"
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);
        assert(messages.size() == 1);
        assert(messages.get(0).getRead().size() == 5);

        // MAKE SURE EVERY USER WHO READ THE MESSAGE IS LISTED
        for (int i = 0; i < 5; i++)
            assert(messages.get(0).getRead().contains("readByManyOthers" + i));

    }

    @Test
    public void markManyAsRead() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("markManyAsRead",
                "markManyAsRead@email.com",
                "markManyAsRead");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "markManyAsRead Group", 201);

        // SEND 10 MESSAGES
        for (int i = 0; i < 10; i++)
            TestingUtils.sendMessageAndExpect(authToken, "Test Message" + i, groupingId, 201);

        // GET LATEST MESSAGES, NONE SHOULD BE READ
        List<GetMessageDomain> messages = TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);
        assert(messages.size() == 10);
        for (GetMessageDomain message : messages)
            assert(message.getRead().size() == 0);

        // GET MESSAGES AGAIN, ALL SHOULD HAVE BEEN MARKED AS "READ"
        messages = TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);
        assert(messages.size() == 10);
        for (GetMessageDomain message : messages) {
            assert (message.getRead().size() == 1);
            assert(message.getRead().get(0).equals("markManyAsRead"));
        }
    }
}
