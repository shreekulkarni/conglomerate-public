package com.conglomerate.dev.integration.groupings;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

import static org.junit.Assert.assertThat;

public class TestEditGroupingNameEndpoint {

    @Test
    public void userGroupingOwner() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("RazanKawai", "r@k.com", "boilerup");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // CHANGE GROUP NAME
        TestingUtils.changeGroupingNameAndExpect(authToken, groupingId, "Changed!", 200);
    }

    @Test
    public void userNotInGrouping() throws Exception {
        // CREATE OWNER ACCOUNT AND AND LOGIN
        String ownerAuth = TestingUtils.createUserAndLoginSuccess("owner", "owner@owner.com", "owner");

        // CREATE OWNER GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(ownerAuth, "Owner's Group", 201);

        // CREATE OTHER USER AND LOGIN
        String otherAuth = TestingUtils.createUserAndLoginSuccess("other user", "other@other.com", "other");

        // TRY TO CHANGE WITH OTHER USERS TOKEN (EXPECT FAIL)
        TestingUtils.changeGroupingNameAndExpect(otherAuth, groupingId, "Changed!", 400);
    }

    @Test
    public void userNotGroupingOwner() throws Exception {
        // CREATE OWNER ACCOUNT AND AND LOGIN
        String ownerAuth = TestingUtils.createUserAndLoginSuccess("ownerWithMultipleUsers", "ownerWithMultipleUsers@owner.com", "ownerWithMultipleUsers");

        // CREATE OWNER GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(ownerAuth, "Multiple Members", 201);

        // CREATE OTHER USER AND LOGIN
        String otherAuth = TestingUtils.createUserAndLoginSuccess("otherUser1", "other1@other1.com", "other");

        // TRY TO JOIN THE GROUP
        TestingUtils.joinGroupingAndExpect(otherAuth, groupingId, 200);

        // CREATE ANOTHER GROUP
        TestingUtils.createGroupingAndExpect(otherAuth, "Other group", 201);

        // TRY TO CHANGE WITH OTHER USERS TOKEN (EXPECT FAIL
        TestingUtils.changeGroupingNameAndExpect(otherAuth, groupingId, "Changed!", 400);
    }
}
