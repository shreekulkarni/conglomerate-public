package com.conglomerate.dev.integration.documents;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import com.conglomerate.dev.models.domain.ListGroupDocsDomain;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;

public class TestSetShared {

    @BeforeClass
    public static void setUp() throws Exception {
        File testFile = new File("test.txt");
        PrintWriter printWrite = new PrintWriter(new FileOutputStream(testFile));
        printWrite.println("This is a test");
        printWrite.close();
    }

    @Test
    public void setSharedTrue() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("setSharedTrue",
                "setSharedTrue@email.com",
                "setSharedTrue");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "setSharedTrue", 201);

        // UPLOAD PRIVATE DOCUMENT
        int documentId = TestingUtils.uploadPrivateDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // CREATE OTHER USER
        String authToken2 = TestingUtils.createUserAndLoginSuccess("setSharedTrue2",
                "setSharedTrue2@email.com",
                "setSharedTrue2");
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        List<ListGroupDocsDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken2, groupingId, 200);
        assert(docs.get(0).getDocuments().size() == 0);

        // SET SHARED = TRUE
        TestingUtils.setSharedAndExpect(authToken, documentId, true, 200);

        docs = TestingUtils.listGroupDocsAndExpect(authToken2, groupingId, 200);
        assert(docs.get(0).getDocuments().size() == 1);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);

    }

    @Test
    public void setSharedFalse() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("setSharedFalse",
                "setSharedFalse@email.com",
                "setSharedFalse");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "setSharedFalse", 201);

        // UPLOAD SHARED DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // CREATE OTHER USER
        String authToken2 = TestingUtils.createUserAndLoginSuccess("setSharedFalse2",
                "setSharedFalse2@email.com",
                "setSharedFalse2");
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        List<ListGroupDocsDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken2, groupingId, 200);
        assert(docs.get(0).getDocuments().size() == 1);

        // SET SHARED = FALSE
        TestingUtils.setSharedAndExpect(authToken, documentId, false, 200);

        docs = TestingUtils.listGroupDocsAndExpect(authToken2, groupingId, 200);
        assert(docs.get(0).getDocuments().size() == 0);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);

    }

    @Test
    public void setSharedSame() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("setSharedSame",
                "setSharedSame@email.com",
                "setSharedSame");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "setSharedSame", 201);

        // UPLOAD SHARED DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // CREATE OTHER USER
        String authToken2 = TestingUtils.createUserAndLoginSuccess("setSharedSame2",
                "setSharedSame2@email.com",
                "setSharedSame2");
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        List<ListGroupDocsDomain> docs = TestingUtils.listGroupDocsAndExpect(authToken2, groupingId, 200);
        assert(docs.get(0).getDocuments().size() == 1);

        // SET SHARED = TRUE
        TestingUtils.setSharedAndExpect(authToken, documentId, true, 200);

        docs = TestingUtils.listGroupDocsAndExpect(authToken2, groupingId, 200);
        assert(docs.get(0).getDocuments().size() == 1);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);

    }

    @Test
    public void setSharedNoDoc() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("setSharedNoDoc",
                "setSharedNoDoc@email.com",
                "setSharedNoDoc");

        // SET SHARED = TRUE
        TestingUtils.setSharedAndExpect(authToken, -1, true, 400);

    }

    @Test
    public void setSharedNotOwner() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("setSharedNotOwner",
                "setSharedNotOwner@email.com",
                "setSharedNotOwner");

        // CREATE GROUP
        int groupingId = TestingUtils.createGroupingAndExpect(authToken, "setSharedNotOwner", 201);

        // UPLOAD SHARED DOCUMENT
        int documentId = TestingUtils.uploadDocumentAndExpect(authToken, groupingId, "test.txt", 201);

        // CREATE OTHER USER
        String authToken2 = TestingUtils.createUserAndLoginSuccess("setSharedNotOwner2",
                "setSharedNotOwner2@email.com",
                "setSharedNotOwner2");
        TestingUtils.joinGroupingAndExpect(authToken2, groupingId, 200);

        // SET SHARED = TRUE
        TestingUtils.setSharedAndExpect(authToken2, documentId, true, 401);

        TestingUtils.deleteDocumentAndExpect(authToken, documentId, 200);

    }

    @Test
    public void setSharedBadAuth() throws Exception {
        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("setSharedNotBadAuth",
                "setSharedBadAuth@email.com",
                "setSharedBadAuth");

        TestingUtils.setSharedAndExpect("bad auth", -1, true, 400);
    }
}
