package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestDeleteDocument {

    @Test
    public void deleteBasic() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteBasic",
                "deleteBasic@email.com",
                "deleteBasic");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteBasic", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // DELETE DOCUMENT
        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);

    }

    @Test
    public void deleteFromFolder() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteFromFolder",
                "deleteFromFolder@email.com",
                "deleteFromFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteFromFolder", 201);

        // CREATE FOLDER
        int folderId = TestingUtils.createFolderAndExpect(authToken, groupingId, "deleteFromFolder", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId,"test.txt", 201);

        // DELETE DOCUMENT
        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);

    }

    @Test
    public void deleteDocNotOwner() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteDocNotOwner1",
                "deleteDocNotOwner1@email.com",
                "deleteDocNotOwner1");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteDocNotOwner", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);
        
        // CREATE OTHER USER
        authToken = TestingUtils.createUserAndLoginSuccess("deleteDocNotOwner2",
                "deleteDocNotOwner2@email.com",
                "deleteDocNotOwner2");

        System.out.println("MANUALLY DELETE DOC WITH GROUP " + groupingId + " AND ID " + documentId);

        // DELETE DOCUMENT
        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 401);
        
    }

    @Test
    public void deleteNoSuchDoc() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deletNoSuchDoc",
                "deleteNoSuchDoc@email.com",
                "deleteNoSuchDoc");

        // DELETE DOCUMENT
        TestingUtils.deleteDocumentAndExpect(authToken, -1, 400);

    }

    @Test
    public void deleteBadAuth() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteBadAuth",
                "deleteBadAuth@email.com",
                "deleteBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteBadAuth", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        System.out.println("MANUALLY DELETE DOC WITH GROUP " + groupingId + " AND ID " + documentId);

        // DELETE DOCUMENT
        TestingUtils.deleteDocumentAndExpect("bad auth", documentId, 400);

    }
}
