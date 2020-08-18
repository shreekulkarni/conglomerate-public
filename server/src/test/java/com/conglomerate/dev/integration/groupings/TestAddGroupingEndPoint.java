package com.conglomerate.dev.integration.groupings;

import com.conglomerate.dev.integration.TestingUtils;
import org.junit.Test;

public class TestAddGroupingEndPoint {

    @Test
    public void addGroupingSuccess() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("addGroupSuccess",
                "addGroupSuccess@email.com",
                "addGroupSuccess");

        // TRY TO ADD GROUP
        TestingUtils.createGroupingAndExpect(authToken,
                "Successful Group",
                201);


    }
}
