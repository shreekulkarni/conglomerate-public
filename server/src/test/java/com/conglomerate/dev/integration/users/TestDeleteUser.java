package com.conglomerate.dev.integration.users;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

import static org.junit.Assert.assertThat;

public class TestDeleteUser {

    @BeforeClass
    public static void setUp() throws Exception {
        File testFile = new File("test.txt");
        PrintWriter printWrite = new PrintWriter(new FileOutputStream(testFile));
        printWrite.println("This is a test");
        printWrite.close();
    }

    @Test
    public void deleteUserSuccess() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserSuccess",
                "deleteUserSuccess@email.com",
                "deleteUserSuccess");

        // TRY TO DELETE USER
        TestingUtils.deleteUserAndExpect(authToken, 200);

    }

    @Test
    public void deleteUserTwice() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserTwice",
                "deleteUserTwice@email.com",
                "deleteUserTwice");

        // DELETE USER
        TestingUtils.deleteUserAndExpect(authToken, 200);

        // TRY TO DELETE USER AGAIN
        TestingUtils.deleteUserAndExpect(authToken, 400);

    }

    @Test
    public void deleteUserInGroup() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserInGroup1",
                "deleteUserInGroup1@email.com",
                "deleteUserInGroup1");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // CREATE USER AND LOGIN
        authToken = TestingUtils.createUserAndLoginSuccess("deleteUserInGroup2",
                "deleteUserInGroup2@email.com",
                "deleteUserInGroup2");

        // JOIN GROUP
        TestingUtils.joinGroupingAndExpect(authToken, groupingId, 200);


        // DELETE USER
        TestingUtils.deleteUserAndExpect(authToken, 200);

    }


    @Test
    public void deleteUserOwnsGroup() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserOwnsGroup",
                "deleteUserOwnsGroup@email.com",
                "deleteUserOwnsGroup");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // DELETE USER
        TestingUtils.deleteUserAndExpect(authToken, 200);

    }


    @Test
    public void deleteUserSentMessages() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserSentMessage",
                "deleteUserSentMessage@email.com",
                "deleteUserSentMessage");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        TestingUtils.sendMessageAndExpect(authToken, "Test message", groupingId, 201);

        // DELETE USER
        TestingUtils.deleteUserAndExpect(authToken, 200);

    }

    @Test
    public void deleteUserReadMessages() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserSentMessage",
                "deleteUserSentMessage@email.com",
                "deleteUserSentMessage");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "New Group", 201);

        // SEND MESSAGE
        TestingUtils.sendMessageAndExpect(authToken, "Test message", groupingId, 201);

        // READ MESSAGE
        TestingUtils.getLatestMessagesAndExpect(authToken, groupingId, 200);

        // DELETE USER
        TestingUtils.deleteUserAndExpect(authToken, 200);

    }

    @Test
    public void deleteUserPublicDoc() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserPublicDoc",
                "deleteUserPublicDoc@email.com",
                "deleteUserPublicDoc");

        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteUserPublicDoc", 201);

        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        TestingUtils.deleteUserAndExpect(authToken, 200);

    }

    @Test
    public void deleteUserPrivateDoc() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserPrivateDoc",
                "deleteUserPrivateDoc@email.com",
                "deleteUserPrivateDoc");

        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteUserPrivateDoc", 201);

        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        TestingUtils.deleteUserAndExpect(authToken, 200);

    }

    @Test
    public void deleteUserDocStays() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserDocStays",
                "deleteUserDocStays@email.com",
                "deleteUserDocStays");

        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteUserDocStays", 201);

        String authToken2 = TestingUtils.createUserAndLoginSuccess("deleteUserDocStays1",
                "deleteUserDocStays1@email.com",
                "deleteUserDocStays1");

        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        int documentId = TestingUtils.uploadDocumentAndExpect(authToken2, groupingId, "test.txt", 201);

        TestingUtils.deleteUserAndExpect(authToken2, 200);

        List<ListDocumentDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200).get(0).getDocuments();
        assert(docs.size() == 1);
        assert(docs.get(0).getUploaderUsername().equals("Deleted Account"));

    }

    @Test
    public void deleteUserDocDeleted() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("deleteUserDocDeleted",
                "deleteUserDocDeleted@email.com",
                "deleteUserDocDeleted");

        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "deleteUserDocDeleted", 201);

        String authToken2 = TestingUtils.createUserAndLoginSuccess("deleteUserDocDeleted1",
                "deleteUserDocDeleted1@email.com",
                "deleteUserDocDeleted1");

        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        int documentId = TestingUtils.uploadPrivateDocumentAndExpect(authToken2, groupingId, "test.txt", 201);

        TestingUtils.deleteUserAndExpect(authToken2, 200);

        List<ListDocumentDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200).get(0).getDocuments();
        assert(docs.size() == 0);

    }

}
