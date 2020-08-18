package com.conglomerate.dev.integration.users;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestAddUserEndPoint {
    @Test
    public void createUserSuccess()
            throws Exception {
        TestingUtils.createUserAndExpect("successfulCreateUser",
                "succesfulCreateUser@gmail.com",
                "successfulPassword",
                201);
    }

    @Test
    public void createUserExistingUsername() throws Exception {
        TestingUtils.createUserAndExpect("existingUsername",
                "createUserExistingUsername@test.com",
                "successfulPassword",
                201);

        TestingUtils.createUserAndExpect("existingUsername",
                "createUserExistingUsernameDifferent@test.com",
                "successfulPassword",
                400);
    }

    @Test
    public void createUserExistingEmail() throws Exception {
        TestingUtils.createUserAndExpect("existingEmail",
                "createUserExistingEmail@test.com",
                "successfulPassword",
                201);

        TestingUtils.createUserAndExpect("existingEmailDifferent",
                "createUserExistingEmail@test.com",
                "successfulPassword",
                400);
    }
}
