package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import org.junit.Test;

import java.util.List;

public class TestSearchForDoc {

    @Test
    public void noMatch() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("noMatch",
                "noMatch@email.com",
                "noMatch");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "No Match", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "1", 200);

        assert(searchResult.isEmpty());
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void oneMatch() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("oneMatch",
                "oneMatch@email.com",
                "oneMatch");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "One Match", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "t", 200);

        System.out.println(searchResult.size());
        assert(searchResult.size() == 1);
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void twoMatches() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("twoMatches",
                "twoMatches@email.com",
                "twoMatches");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "Two Matches", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // UPLOAD DOCUMENT
        int documentId2 = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test2.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "t", 200);

        System.out.println(searchResult.size());
        assert(searchResult.size() == 2);
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
        TestingUtils.deleteDocumentAndExpect(authToken, documentId2, 200);
    }

    @Test
    public void oneMatchInFolder() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("oneMatchInFolder",
                "oneMatchInFolder@email.com",
                "oneMatchInFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "oneMatchInFolder", 201);

        // CREATE FOLDER
        int folderId = TestingUtils.createFolderAndExpect(authToken, groupingId, "oneMatchInFolder", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId,"test.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "t", 200);

        System.out.println(searchResult.size());
        assert(searchResult.size() == 1);
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
    }

    @Test
    public void twoMatchesInFolder() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("twoMatchesInFolder",
                "twoMatchesInFolder@email.com",
                "twoMatchesInFolder");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "twoMatchesInFolder", 201);

        // CREATE FOLDER
        int folderId = TestingUtils.createFolderAndExpect(authToken, groupingId, "twoMatchesInFolder", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId,"test.txt", 201);

        // UPLOAD DOCUMENT
        int documentId2 = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId,"test2.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "t", 200);

        System.out.println(searchResult.size());
        assert(searchResult.size() == 2);
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
        TestingUtils.deleteDocumentAndExpect(authToken, documentId2, 200);
    }

    @Test
    public void oneMatchInFolderOneWithout() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("oneMatchInFolderOneWithout",
                "oneMatchInFolderOneWithout@email.com",
                "oneMatchInFolderOneWithout");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "oneMatchInFolderOneWithout", 201);

        // CREATE FOLDER
        int folderId = TestingUtils.createFolderAndExpect(authToken, groupingId, "oneMatchInFolderOneWithout", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentToFolderAndExpect(authToken, groupingId, folderId,"test.txt", 201);

        // UPLOAD DOCUMENT
        int documentId2 = TestingUtils.uploadDocumentAndExpect(authToken, groupingId,"test2.txt", 201);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "t", 200);

        System.out.println(searchResult.size());
        assert(searchResult.size() == 2);
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
        TestingUtils.deleteDocumentAndExpect(authToken, documentId2, 200);
    }

    @Test
    public void onePublicMatch() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("onePublicMatch",
                "onePublicMatch@email.com",
                "onePublicMatch");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "onePublicMatch", 201);

        // UPLOAD DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // UPLOAD DOCUMENT
        int documentId2 = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test2.txt", 201);
        TestingUtils.setSharedAndExpect(authToken, documentId2, false, 200);

        // LIST DOCUMENTS AND ENSURE THE CORRECT RESPONSE
        List<ListDocumentDomain> searchResult = TestingUtils.searchForDocumentAndExpect(authToken, groupingId, "t", 200);

        System.out.println(searchResult.size());
        assert(searchResult.size() == 1);
        System.out.println(searchResult);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);
        TestingUtils.deleteDocumentAndExpect(authToken, documentId2, 200);
    }
}
