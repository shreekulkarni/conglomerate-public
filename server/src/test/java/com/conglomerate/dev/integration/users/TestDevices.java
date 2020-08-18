package com.conglomerate.dev.integration.users;

import com.conglomerate.dev.integration.TestingUtils;
import com.conglomerate.dev.models.Device;
import org.junit.Test;

import java.util.List;

public class TestDevices {
    @Test
    public void registerDeviceSuccess() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("registerDeviceSuccess",
                "registerDeviceSuccess@email.com",
                "registerDeviceSuccess");

        // REGISTER DEVICE TOKEN AND EXPECT 201 (CREATED)
        TestingUtils.registerDeviceAndExpect(authToken, "test_token", 201);

    }

    @Test
    public void registerDeviceNoAuth() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("registerDeviceNoAuth",
                "registerDeviceNoAuth@email.com",
                "registerDeviceNoAuth");

        // REGISTER DEVICE TOKEN AND EXPECT 201 (CREATED)
        TestingUtils.registerDeviceAndExpect("bad auth token", "test_token", 400);
    }

    @Test
    public void getDevicesOne() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getDevicesOne",
                "getDevicesOne@email.com",
                "getDevicesOne");

        // REGISTER DEVICE TOKEN AND EXPECT 201 (CREATED)
        TestingUtils.registerDeviceAndExpect(authToken, "test_token", 201);

        // GET DEVICES AND ENSURE CORRECT RESPONSE
        List<Device> devices = TestingUtils.getDevicesAndExpect(authToken, 200);
        assert(devices.size() == 1);
        System.out.println(devices);
        System.out.println(devices.get(0).getDeviceToken());
        assert(devices.get(0).getDeviceToken().equals("test_token"));

    }

    @Test
    public void getDevicesTwo() throws Exception {

        // CREATE USER AND LOGIN
        String authToken = TestingUtils.createUserAndLoginSuccess("getDevicesTwo",
                "getDevicesTwo@email.com",
                "getDevicesTwo");

        // REGISTER DEVICE TOKEN AND EXPECT 201 (CREATED)
        TestingUtils.registerDeviceAndExpect(authToken, "test_token", 201);
        TestingUtils.registerDeviceAndExpect(authToken, "second_token", 201);

        // GET DEVICES AND ENSURE THEY'RE CORRECT
        List<Device> devices = TestingUtils.getDevicesAndExpect(authToken, 200);
        assert(devices.size() == 2);
        assert(devices.get(0).getDeviceToken().equals("test_token"));
        assert(devices.get(1).getDeviceToken().equals("second_token"));

    }
}
