package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.repositories.GroupingRepository;
import com.conglomerate.dev.services.GroupingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class GroupingControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupingService groupingService;

    @MockBean
    private GroupingRepository groupingRepository;

    private Set<User> dummyMembers;

    private List<Grouping> dummyGroupings;

    @Before
    public void setUp() {
        List<User> dummyUsers = Arrays.asList(
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

        dummyMembers = new HashSet<User>();
        dummyMembers.addAll(dummyUsers);

        dummyGroupings = Arrays.asList(
                Grouping.builder()
                        .id(1)
                        .name("Group 1")
                        .creationDate(LocalDateTime.now())
                        .members(dummyMembers)
                        .build(),
                Grouping.builder()
                        .id(2)
                        .name("Group 2")
                        .creationDate(LocalDateTime.now())
                        .build()
        );
    }

    @Test
    public void getAllUsers() throws Exception {
        // given
        Mockito.when(groupingService.getAllGroupings()).thenReturn(dummyGroupings);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        System.out.println(objectMapper.writeValueAsString(dummyGroupings));

        // when + then
        mockMvc.perform(get("/groupings"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dummyGroupings)));

        // make sure that the /users returns the proper representation of the findAllUsers
    }
}
