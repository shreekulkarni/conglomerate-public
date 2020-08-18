package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestCreateFolder {

    @Test
    public void createFolderSuccess() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createFolderSuccess",
                "createFolderSuccess@email.com",
                "createFolderSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Create Folder Success", 201);

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken, groupingId, "Success", 201);
    }

    @Test
    public void createFolderBadAuth() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createFolderBadAuth",
                "createFolderBadAuth@email.com",
                "createFolderBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Create Folder Bad Auth", 201);

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect("Bad auth token", groupingId, "Fail", 400);
    }

    @Test
    public void createFolderNoGroup() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createFolderNoGroup",
                "createFolderNoGroup@email.com",
                "createFolderNoGroup");

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken, -1, "Fail", 400);
    }

    @Test
    public void createFolderNotMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("createFolderNotMember",
                "createFolderNotMember@email.com",
                "createFolderNotMember");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Create Folder Not Member", 201);

        // CREATE USER AND LOGIN
        String authToken2 = TestingUtils.createUserAndLoginSuccess("createFolderNotMember2",
                "createFolderNotMember2@email.com",
                "createFolderNotMember2");

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken2, groupingId, "createFolderNotMember", 400);
    }


}
