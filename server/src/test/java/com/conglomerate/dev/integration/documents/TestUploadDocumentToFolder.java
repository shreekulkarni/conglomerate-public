package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class TestUploadDocumentToFolder {

    @BeforeClass
    public static void setUp() throws Exception {
        File testFile = new File("test.txt");
        PrintWriter printWrite = new PrintWriter(new FileOutputStream(testFile));
        printWrite.println("This is a test");
        printWrite.close();
    }

    @Test
    public void uploadToFolderSuccess() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadToFolderSuccess",
                "uploadToFolderSuccess@email.com",
                "uploadToFolderSuccess");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "uploadToFolderSuccess group", 201);

        // CREATE FOLDER
        int folderId = TestingUtils.createFolderAndExpect(authToken, groupingId, "uploadToFolderSuccess folder", 201);
        System.out.println("Folder Id Got Back: " + folderId);

        // UPLOAD
        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId, "test.txt", 201);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void uploadToFolderNoSuchFolder() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadToFolderNoSuchFolder",
                "uploadToFolderNoSuchFolder@email.com",
                "uploadToFolderNoSuchFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "uploadToFolderNoSuchFolder", 201);

        // UPLOAD AND EXPECT ERROR
        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, -1, "test.txt", 400);
    }

    @Test
    public void uploadToFolderNoGroup() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("uploadToFolderNoGroup",
                "uploadToFolderNoGroup@email.com",
                "uploadToFolderNoGroup");

        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, -1, 1, "test.txt", 400);

    }

    @Test
    public void uploadToFolderBadAuth() throws Exception {
        TestingUtils.uploadDocumentToFolderAndExpect("Bad Auth", -1, -1, "test.txt", 400);
    }

}
