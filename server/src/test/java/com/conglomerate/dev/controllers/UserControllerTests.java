package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.User;
import com.conglomerate.dev.repositories.UserRepository;
import com.conglomerate.dev.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    private List<User> dummyUsers = Arrays.asList(
            User.builder()
                    .id(1)
                    .userName("lukelavin")
                    .email("llavin@purdue.edu")
                    .passwordHash("no thank you")
                    .calendarLink(null)
                    .profilePic(null)
                    .build(),
            User.builder()
                    .id(2)
                    .userName("lukelavin")
                    .email("llavin@purdue.edu")
                    .passwordHash("no thank you")
                    .calendarLink(null)
                    .profilePic(null)
                    .build()
    );

    @Test
    public void getAllUsers() throws Exception {
        // given
        Mockito.when(userService.getAllUsers()).thenReturn(dummyUsers);

        // when + then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(new ObjectMapper().writeValueAsString(dummyUsers)));

        // make sure that the /users returns the proper representation of the findAllUsers
    }
}
