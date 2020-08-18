package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.*;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import com.conglomerate.dev.models.domain.ListFolderDomain;
import com.conglomerate.dev.models.domain.ListGroupDocsDomain;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

public class TestListDocs {

    @BeforeClass
    public static void setUp() throws Exception {
        File testFile = new File("test.txt");
        PrintWriter printWrite = new PrintWriter(new FileOutputStream(testFile));
        printWrite.println("This is a test");
        printWrite.close();
    }

    @Test
    public void listOneFolder() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listOneFolder",
                "listOneFolder@email.com",
                "listOneFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "List One Folder", 201);

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken, groupingId, "List One Folder", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListGroupDocsDomain> listGroupDocsDomain = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200);
        List<ListFolderDomain> folders = listGroupDocsDomain.get(0).getFolders();
        List<ListDocumentDomain> documents = listGroupDocsDomain.get(0).getDocuments();

        assert(documents.isEmpty());

        assert(folders.size() == 1);
        System.out.println(folders);
        System.out.println(folders.get(0).getName());
        //assert (folders.get(0).getName().equals("List One Folder"));
    }

    @Test
    public void listOneDocument() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listOneDocument",
                "listOneDocument@email.com",
                "listOneDocument");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "List One Document", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListGroupDocsDomain> listGroupDocsDomain = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200);

        List<ListDocumentDomain> documents = listGroupDocsDomain.get(0).getDocuments();
        List<ListFolderDomain> folders = listGroupDocsDomain.get(0).getFolders();

        assert(documents.size() == 1);
        assert(folders.isEmpty());


        System.out.println(documents);
        System.out.println(documents.get(0).getDocumentLink());
        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void listOneDocOneFolder() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listOneDocOneFolder",
                "listOneDocOneFolder@email.com",
                "listOneDocOneFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listOneDocOneFolder", 201);

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken, groupingId, "listOneDocOneFolder", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListGroupDocsDomain> listGroupDocsDomain = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200);

        List<ListDocumentDomain> documents = listGroupDocsDomain.get(0).getDocuments();
        List<ListFolderDomain> folders = listGroupDocsDomain.get(0).getFolders();

        assert(documents.size() == 1);
        assert(folders.size() == 1);

        System.out.println(folders);
        System.out.println(folders.get(0).getName());

        System.out.println(documents);
        System.out.println(documents.get(0).getDocumentLink());
        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void listMultiple() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listMultiple",
                "listMultiple@email.com",
                "listMultiple");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listMultiple", 201);

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken, groupingId, "listMultiple", 201);

        // CREATE FOLDER
        TestingUtils.createFolderAndExpect(authToken, groupingId, "listMultiple2", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // UPLOAD DOCUMENT
        int documentId2 = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test2.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListGroupDocsDomain> listGroupDocsDomain = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200);

        List<ListDocumentDomain> documents = listGroupDocsDomain.get(0).getDocuments();
        List<ListFolderDomain> folders = listGroupDocsDomain.get(0).getFolders();

        assert(documents.size() == 2);
        assert(folders.size() == 2);

        System.out.println(folders);
        System.out.println(folders.get(0).getName());
        System.out.println(folders.get(1).getName());

        System.out.println(documents);
        System.out.println(documents.get(0).getDocumentLink());
        System.out.println(documents.get(1).getDocumentLink());

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
        TestingUtils.deleteDocumentAndExpect(authToken, documentId2, 200);
    }

    @Test
    public void listWithDocumentsInFolder() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("listWithDocumentsInFolder",
                "listWithDocumentsInFolder@email.com",
                "listWithDocumentsInFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "listWithDocumentsInFolder", 201);

        // CREATE FOLDER
        int folderId = TestingUtils.createFolderAndExpect(authToken, groupingId, "listWithDocumentsInFolder", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // UPLOAD DOCUMENT
        int documentId2 = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId, "test2.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListGroupDocsDomain> listGroupDocsDomain = TestingUtils.listGroupDocsAndExpect(authToken, groupingId, 200);

        List<ListDocumentDomain> documents = listGroupDocsDomain.get(0).getDocuments();
        List<ListFolderDomain> folders = listGroupDocsDomain.get(0).getFolders();

        assert(documents.size() == 1);
        assert(folders.size() == 1);
        assert(folders.get(0).getDocuments().size() == 1);

        System.out.println(folders);
        System.out.println(folders.get(0).getName());

        System.out.println(documents);
        System.out.println(documents.get(0).getDocumentLink());
        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
        TestingUtils.deleteDocumentAndExpect(authToken, documentId2, 200);
    }



}
