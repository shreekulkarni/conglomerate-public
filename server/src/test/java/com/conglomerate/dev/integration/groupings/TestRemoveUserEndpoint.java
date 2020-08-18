package com.conglomerate.dev.integration.groupings;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestRemoveUserEndpoint {
    @Test
    public void userNotInGrouping() throws Exception {
        // CREATE USER
        TestingUtils.createUserAndExpect("RandomNotInGroup", "RandomNotInGroup@email.com", "password", 201);
        // CREATE USER AND LOGIN
        String OwnerAuthToken = TestingUtils.createUserAndLoginSuccess("OwnerOfGroupFailsTest", "ownerOfGroup@removesMemberFails.com", "group_owner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(OwnerAuthToken, "Remove Member Fail", 201);

        // OWNER REMOVES GROUPING_MEMBER_TO_REMOVE
        TestingUtils.removeMemberAndExpect(OwnerAuthToken, groupingId, "RandomNotInGroup",400);
    }

    @Test
    public void userNotGroupingOwner()
            throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("removeMemberOwner", "removeMember@owner.com", "boilerup");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "remove member test", 201);

        // CREATE USER AND LOGIN
        String authToken2 = TestingUtils.createUserAndLoginSuccess("removeMemberfail", "removeMember@fail.com", "group_member");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        // CREATE USER AND LOGIN
        String authToken3 = TestingUtils.createUserAndLoginSuccess("removeMemberFailExtra", "removeMemberFail@extra.com", "group_member");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken3, groupingId, 200);

        // ATTEMPT TO REMOVE USER REMOVE_MEMBER_FAIL
        TestingUtils.removeMemberAndExpect(authToken3, groupingId, "removeMemberfail", 400);
    }

    @Test
    public void userGroupingOwner()
            throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("OwnerOfGroup", "owner@removesMember.com", "group_owner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Remove Member Success", 201);

        // CREATE USER AND LOGIN
        String authToken2 = TestingUtils.createUserAndLoginSuccess("GroupingMemberToRemove", "grp@remove.com", "group_member");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        // OWNER REMOVES GROUPING_MEMBER_TO_REMOVE
        TestingUtils.removeMemberAndExpect(authToken, groupingId, "GroupingMemberToRemove",200);
    }

}
