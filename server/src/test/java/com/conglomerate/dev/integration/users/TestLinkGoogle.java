package com.conglomerate.dev.integration.users;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestLinkGoogle {
    @Test
    public void linkGoogleSuccess() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("linkGoogleSuccess",
                "linkGoogleSuccess@email.com",
                "linkGoogleSuccess");

        // LINK GOOGLE (200 success)
        int userId = TestingUtils.linkGoogleAndExpect(authToken, "id", "refresh", 200);


    }

    @Test
    public void linkGoogleBadAuth() throws Exception {

        // CREATE USER AND LOGIN
        TestingUtils.createUserAndLoginSuccess("linkGoogleBadAuth",
                "linkGoogleBadAuth@email.com",
                "linkGoogleBadAuth");

        // LINK GOOGLE (400 ERROR)
        int userId = TestingUtils.linkGoogleAndExpect("bad auth", "id", "refresh", 400);
    }
}
