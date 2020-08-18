package com.conglomerate.dev.integration.groupings;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import org.junit.Test;

import java.util.List;

public class TestLeaveGroupingEndpoint {
    @Test
    public void userGroupingOwnerOnlyMember()
            throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("Razan", "razan@k.com", "boilerup");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

//        // LEAVE GROUP
       TestingUtils.leaveGroupingAndExpect(authToken, groupingId, 200);
    }

    @Test
    public void userGroupingOwnerNotOnlyMember()
            throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("GroupingOwner", "grp@own.com", "group_owner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // CREATE USER AND LOGIN
        String authToken2 = TestingUtils.createUserAndLoginSuccess("GroupingMember", "grp@mem.com", "group_member");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        // CREATE USER AND LOGIN
        String authToken3 = TestingUtils.createUserAndLoginSuccess("GroupingMember2", "grp2@mem.com", "group_member");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken3, groupingId, 200);

        // OWNER LEAVES GROUP
        TestingUtils.leaveGroupingAndExpect(authToken, groupingId, 200);
    }

    @Test
    public void userNotGroupingOwner()
            throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("UsrOwnsGroup", "usr@g.com", "user_owns_group");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // CREATE USER AND LOGIN
        String authToken2 = TestingUtils.createUserAndLoginSuccess("GrpMember", "grp@m.com", "group_member");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        // Member LEAVES GROUP
        TestingUtils.leaveGroupingAndExpect(authToken2, groupingId, 200);
    }


    @Test
    public void userNotGroupingMember()
            throws Exception {

        // CREATE OWNER ACCOUNT AND AND LOGIN
        String ownerAuth = TestingUtils.createUserAndLoginSuccess("ownerGrp", "ownerGrp@owner.com", "owner");

        // CREATE OWNER GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(ownerAuth, "Owner's Group", 201);

        // CREATE OTHER USER AND LOGIN
        String otherAuth = TestingUtils.createUserAndLoginSuccess("other_user_2", "other2@other.com", "other");

        // TRY TO LEAVE GROUP
        TestingUtils.leaveGroupingAndExpect(otherAuth, groupingId, 400);
    }

    @Test
    public void leaveGroupDocStays() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("leaveGroupDocStays",
                "leaveGroupDocStays@email.com",
                "leaveGroupDocStays");

        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "leaveGroupDocStays", 201);

        String authToken2 = TestingUtils.createUserAndLoginSuccess("leaveGroupDocStays1",
                "leaveGroupDocStays1@email.com",
                "leaveGroupDocStays1");

        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        int documentId = TestingUtils.uploadDocumentAndExpect(authToken2, groupingId, "test.txt", 201);

        TestingUtils.leaveGroupingAndExpect(authToken2, groupingId, 200);

        List<ListDocumentDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200).get(0).getDocuments();
        assert(docs.size() == 1);
        assert(docs.get(0).getUploaderUsername().equals("Deleted Account"));

    }

    @Test
    public void leaveGroupDocDeleted() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("leaveGroupDocDeleted",
                "leaveGroupDocDeleted@email.com",
                "leaveGroupDocDeleted");

        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "leaveGroupDocDeleted", 201);

        String authToken2 = TestingUtils.createUserAndLoginSuccess("leaveGroupDocDeleted1",
                "leaveGroupDocDeleted1@email.com",
                "leaveGroupDocDeleted1");

        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        int documentId = TestingUtils.uploadPrivateDocumentAndExpect(authToken2, groupingId, "test.txt", 201);

        TestingUtils.leaveGroupingAndExpect(authToken2, groupingId,200);

        List<ListDocumentDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200).get(0).getDocuments();
        assert(docs.size() == 0);

    }
}
