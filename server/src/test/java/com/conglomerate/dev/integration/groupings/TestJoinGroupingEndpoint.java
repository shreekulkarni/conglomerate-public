package com.conglomerate.dev.integration.groupings;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class TestJoinGroupingEndpoint {
    @Test
    public void joinGroupSuccess() throws Exception {

        // CREATE GROUP WITH RANDOM OWNER
        int groupingId = TestingUtils.createGroupingSuccess("joinGroupSuccess");

        // CREATE OTHER USER TO JOIN
        String authToken = TestingUtils.createUserAndLoginSuccess("joinGroupSuccess",
                "joinGroupSuccess@email.com",
                "joinGroupSuccess");

        // TRY TO JOIN THE GROUP
        TestingUtils.joinGroupingAndExpect(authToken, groupingId, 200);

    }

    @Test
    public void joinGroupAlreadyIn() throws Exception {

        // CREATE GROUP WITH RANDOM OWNER
        int groupingId = TestingUtils.createGroupingSuccess("joinGroupAlreadyIn");

        // CREATE OTHER USER TO JOIN
        String authToken = TestingUtils.createUserAndLoginSuccess("joinGroupAlreadyIn",
                "joinGroupAlreadyIn@email.com",
                "joinGroupAlreadyIn");

        // TRY TO JOIN THE GROUP
        TestingUtils.joinGroupingAndExpect(authToken, groupingId, 200);

        // TRY TO JOIN AGAIN
        TestingUtils.joinGroupingAndExpect(authToken, groupingId, 400);
    }

    @Test
    public void joinInvalidGroup() throws Exception {
        // AN INVALID GROUP ID
        int groupingId = -1;

        // CREATE OTHER USER TO JOIN
        String authToken = TestingUtils.createUserAndLoginSuccess("joinInvalidGroup",
                "joinInvalidGroup@email.com",
                "joinInvalidGroup");

        // TRY TO JOIN THE GROUP
        TestingUtils.joinGroupingAndExpect(authToken, groupingId, 400);
    }
}
