package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestSetViewPermissions {

    /*
    @Test
    public void badAuthToken() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("viewPermBadAuth",
                "viewPermBadAuth@email.com",
                "viewPermBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "viewPermBadAuth", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // SET VIEW PERMISSION
        TestingUtils.setViewPermissionsAndExpect("bad auth token", documentId, true, 400);
    }

    @Test
    public void noSuchDocument() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("viewPermNoSuchDoc",
                "viewPermNoSuchDoc@email.com",
                "viewPermNoSuchDoc");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "viewPermNoSuchDoc", 201);


        // SET VIEW PERMISSION
        TestingUtils.setViewPermissionsAndExpect(authToken, -1, true, 400);
    }

    @Test
    public void notDocumentUploader() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("viewPermNotDocUploader",
                "viewPermNotDocUploader@email.com",
                "viewPermNotDocUploader");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "viewPermNotDocUploader", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // CREATE OTHER USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("viewPermNotDocUpOther",
                "viewPermNotDocUpOther@email.com",
                "viewPermNotDocUpOther");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(otherToken, groupingId, 200);

        // SET VIEW PERMISSION
        TestingUtils.setViewPermissionsAndExpect(otherToken, documentId, true, 401);
    }

    @Test
    public void onlyUploader() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("viewPermOnlyOwner",
                "viewPermOnlyOwner@email.com",
                "viewPermOnlyOwner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "viewPermOnlyUploader", 201);

        // CREATE OTHER USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("viewPermOnlyOther",
                "viewPermOnlyOther@email.com",
                "viewPermOnlyOther");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(otherToken, groupingId, 200);


        // CREATE OTHER USER AND LOGIN
        String uploaderToken = TestingUtils.createUserAndLoginSuccess("viewPermOnlyUploader",
                "viewPermOnlyUploader@email.com",
                "viewPermOnlyUploader");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(uploaderToken, groupingId, 200);


        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(uploaderToken, groupingId, "test.txt", 201);

        // SET VIEW PERMISSION
        int response = TestingUtils.setViewPermissionsAndExpect(uploaderToken, documentId, true, 200);

        assert(response == 0);
    }

    @Test
    public void allMembers() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("viewPermAllOwner",
                "viewPermAllOwner@email.com",
                "viewPermAllOwner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "viewPermAllMembers", 201);

        // CREATE OTHER USER AND LOGIN
        String otherToken = TestingUtils.createUserAndLoginSuccess("viewPermAllOther",
                "viewPermAllOther@email.com",
                "viewPermAllOther");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(otherToken, groupingId, 200);


        // CREATE OTHER USER AND LOGIN
        String uploaderToken = TestingUtils.createUserAndLoginSuccess("viewPermAllUploader",
                "viewPermAllUploader@email.com",
                "viewPermAllUploader");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(uploaderToken, groupingId, 200);


        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(uploaderToken, groupingId, "test.txt", 201);

        // SET VIEW PERMISSION
        int response = TestingUtils.setViewPermissionsAndExpect(uploaderToken, documentId, false, 200);

        assert(response == 1);
    }

    */
}
