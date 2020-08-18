package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.Message;
import com.conglomerate.dev.models.domain.GetMessageDomain;
import com.conglomerate.dev.models.domain.RemoveMemberDomain;
import com.conglomerate.dev.models.domain.UpdateGroupingNameDomain;
import com.conglomerate.dev.services.GroupingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
// the server will look to map requests that start with "/groupings" to the endpoints in this controller
@RequestMapping(value = "/groupings", produces = "application/json; charset=utf-8")
public class GroupingController {
    private final GroupingService groupingService;

    @Autowired
    public GroupingController(GroupingService groupingService) {
        this.groupingService = groupingService;
    }

    // @GetMapping maps HTTP GET requests on the endpoint to this method
    // Because no url value has been specified, this is mapping the class-wide "/groups" url
    @GetMapping
    public List<Grouping> getAllGroupings() {
        // Have the logic in GroupService
        // Ideally, GroupController should just control the request mappings
        return groupingService.getAllGroupings();
    }

    @GetMapping(value = "/for-user")
    public List<Grouping> getUsersGroupings(@RequestHeader("authorization") String authHeader) {
        String authToken = authHeader.substring(7);
        return groupingService.getUsersGroupings(authToken);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int addGrouping(@RequestHeader("authorization") String authHeader,
                           @RequestBody String groupName) {
        String authToken = authHeader.substring(7);
        return groupingService.addGrouping(authToken, groupName);
    }

    @PostMapping(value = "/change-name")
    public String updateGroupingName(@RequestHeader("authorization") String authHeader,
                                     @RequestBody UpdateGroupingNameDomain updateGroupingNameDomain) {
        String authToken = authHeader.substring(7);
        return groupingService.updateGroupingName(authToken, updateGroupingNameDomain);
    }

    @PostMapping(value = "/join")
    public int joinGrouping(@RequestHeader("authorization") String authHeader,
                            @RequestBody int groupingId) {
        String authToken = authHeader.substring(7);
        return groupingService.joinGrouping(authToken, groupingId);
    }

    @GetMapping(value = "{id}/messages")
    public List<GetMessageDomain> getLatestMessages(@RequestHeader("authorization") String authHeader,
                                                    @PathVariable int id) {
        String authToken = authHeader.substring(7);
        return groupingService.getLatestMessages(authToken, id);
    }

    @PostMapping(value = "/leave")
    public int leaveGrouping(@RequestHeader("authorization") String authHeader,
                             @RequestBody int groupingId) {
        String authToken = authHeader.substring(7);
        return groupingService.leaveGrouping(authToken, groupingId);
    }

    @PostMapping(value = "/remove-member")
    public String removeMember(@RequestHeader("authorization") String authHeader,
                               @RequestBody RemoveMemberDomain removeMemberDomain) {
        String authToken = authHeader.substring(7);
        return groupingService.removeMember(authToken, removeMemberDomain);
    }
}