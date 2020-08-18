package com.conglomerate.dev.integration.users;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestLogin {

    @Test
    public void loginSuccess()
            throws Exception {
        // CREATE USER
        TestingUtils.createUserAndExpect("loginSuccess",
                "loginSuccess@email.com",
                "loginSuccess",
                201);

        // TRY TO LOGIN
        String authToken = TestingUtils.loginAndExpect("loginSuccess", "loginSuccess", 200);
    }

    @Test
    public void loginWrongPassword()
            throws Exception {
        // CREATE USER
        TestingUtils.createUserAndExpect("loginWrongPassword",
                "loginWrongPassword@email.com",
                "loginWrongPassword",
                201);

        // TRY TO LOGIN WITH INCORRECT CREDENTIALS
        String authToken = TestingUtils.loginAndExpect("loginWrongPassword", "oops", 400);
    }
}
