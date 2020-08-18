package com.conglomerate.dev.integration.users;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestHasLinked {

    @Test
    public void hasLinkedNo() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("hasLinkedNo",
                "hasLinkedNo@email.com",
                "hasLinkedNo");

        // TEST IF LINKED
        assert(!TestingUtils.checkIfLinkedAndExpect(authToken, 200));
    }

    @Test
    public void hasLinkedYes() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("hasLinkedYes",
                "hasLinkedYes@email.com",
                "hasLinkedYes");

        // LINK
        TestingUtils.linkGoogleAndExpect(authToken,"id", "refresh", 200);

        // TEST IF LINKED
        assert(TestingUtils.checkIfLinkedAndExpect(authToken, 200));
    }

    @Test
    public void hasLinkedBadAuth() throws Exception {

        // TEST IF LINKED
        assert(!TestingUtils.checkIfLinkedAndExpect("bad auth", 400));
    }
}
