package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Device;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.AddUserDomain;
import com.conglomerate.dev.models.domain.GoogleTokenDomain;
import com.conglomerate.dev.models.domain.LoginRequestDomain;
import com.conglomerate.dev.models.domain.NewPasswordDomain;
import com.conglomerate.dev.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
// the server will look to map requests that start with "/users" to the endpoints in this controller
@RequestMapping(value = "/users", produces = "application/json; charset=utf-8")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // @GetMapping maps HTTP GET requests on the endpoint to this method
    // Because no url value has been specified, this is mapping the class-wide "/users" url
    @GetMapping
    public List<User> getAllUsers() {
        // Have the logic in UserService
        // Ideally, UserController should just control the request mappings
        return userService.getAllUsers();
    }

    // @PostMapping maps the HTTP put requests on the endpoint to this method
    // it calls the service method and ultimately adds the user to the database
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddUserDomain addUser(@RequestBody AddUserDomain user) {
        return userService.addUser(user);
    }

    @PostMapping(value = "/login")
    public String login(@RequestBody LoginRequestDomain loginRequestDomain) {
        return userService.login(loginRequestDomain);
    }

    @DeleteMapping(value = "/delete-account")
    public void deleteAccount(@RequestHeader("authorization") String authHeader) {
        String authToken = authHeader.substring(7);
        userService.deleteAccount(authToken);
    }

    @GetMapping(value = "/devices")
    public List<Device> getDevices(@RequestHeader("authorization") String authHeader) {
        String authToken = authHeader.substring(7);
        return userService.getDevices(authToken);
    }

    @PostMapping(value = "/devices")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerDevice(@RequestHeader("authorization") String authHeader,
                               @RequestBody String deviceToken){
        String authToken = authHeader.substring(7);
        userService.registerDevice(authToken, deviceToken);
    }

    @GetMapping(value = "/profile-picture")
    public String getProfilePicture(@RequestHeader("authorization") String authHeader) {
        String authToken = authHeader.substring(7);
        return userService.getProfilePicture(authToken);
    }

    @PostMapping(value = "/profile-picture")
    public void setProfilePicture(@RequestHeader("authorization") String authHeader, @RequestBody String newProfilePicture) {
        String authToken = authHeader.substring(7);
        userService.setProfilePicture(authToken, newProfilePicture);
    }

    @GetMapping(value= "/username/{username}/exists")
//    @GetMapping(value= "/{username}/exists")
    public boolean userNameExists(@PathVariable String username){
        return userService.check_username_exists(username);
    }

    @GetMapping(value= "/email/{email}/exists")
    public boolean userEmailExists(@PathVariable String email){
        return userService.check_email_exists(email);
    }

    @PostMapping(value = "{username}/forgot-password")
    public void forgotPassword(@PathVariable String username, @RequestBody String email) {
        userService.forgotPassword(username, email);
    }

    @PostMapping(value = "/{username}/update-password")
    public void updatePassword(@PathVariable String username, @RequestBody NewPasswordDomain newPasswordDomain) {
        userService.updatePassword(username, newPasswordDomain);
    }

    @PostMapping(value = "/link-google")
    public int linkGoogleAccount(@RequestHeader("authorization") String authHeader, GoogleTokenDomain tokens) {
        String authToken = authHeader.substring(7);

        return userService.linkGoogleAccount(authToken, tokens);
    }

    @GetMapping(value = "/has-linked")
    public boolean hasLinkedGoogleAccount(@RequestHeader("authorization") String authHeader) {
        String authToken = authHeader.substring(7);

        return userService.hasLinkedGoogleAccount(authToken);
    }

    @GetMapping(value = "/unlink-google")
    public int unlinkgoogleAccount(@RequestHeader("authorization") String authHeader) {
        String authToken = authHeader.substring(7);

        return userService.unlinkGoogleAccount(authToken);
    }
}
