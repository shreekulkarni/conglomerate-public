package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class TestUploadDocument {

    @BeforeClass
    public static void setUp() throws Exception {
        File testFile = new File("test.txt");
        PrintWriter printWrite = new PrintWriter(new FileOutputStream(testFile));
        printWrite.println("This is a test");
        printWrite.close();
    }

    @Test
    public void uploadDocumentOwner() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentOwner",
                "uploadDocumentOwner@email.com",
                "uploadDocumentOwner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Upload Document Owner", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void uploadDocumentMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentMember1",
                "uploadDocumentMember1@email.com",
                "uploadDocumentMember1");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Upload Document Member", 201);

        // CREATE USER AND LOGIN
        authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentMember2",
                "uploadDocumentMember2@email.com",
                "uploadDocumentMember2");

        TestingUtils.joinGroupingAndExpect(authToken, groupingId, 200);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void uploadDocumentNotMember() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentNotMember1",
                "uploadDocumentNotMember1@email.com",
                "uploadDocumentNotMember1");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Upload Document Not Member", 201);

        // CREATE USER AND LOGIN
        authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentNotMember2",
                "uploadDocumentNotMember2@email.com",
                "uploadDocumentNotMember2");

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 400);
    }

    @Test
    public void uploadDocumentNoGroup() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentNoGroup",
                "uploadDocumentNoGroup@email.com",
                "uploadDocumentNoGroup");

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, -1, "test.txt", 400);
    }

    @Test
    public void uploadDocumentBadAuth() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadDocumentBadAuth",
                "uploadDocumentBadAuth@email.com",
                "uploadDocumentBadAuth");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Upload Document BadAuth", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect("bad auth token", groupingId, "test.txt", 400);

    }
}
