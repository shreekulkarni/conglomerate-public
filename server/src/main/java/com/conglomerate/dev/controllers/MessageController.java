package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Message;
import com.conglomerate.dev.models.domain.SendMessageDomain;
import com.conglomerate.dev.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/messages", produces = "application/json; charset=utf-8")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public List<Message> getMessages() {
        return messageService.getAllMessages();
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public int sendMessage(@RequestHeader("authorization") String authHeader,
                            @RequestBody SendMessageDomain sendMessageDomain) {
        String authToken = authHeader.substring(7);
        return messageService.sendMessage(authToken, sendMessageDomain);
    }

    @PostMapping(value = "/{messageId}/like")
    public int likeMessage(@RequestHeader("authorization") String authHeader,
                             @PathVariable int messageId) {
        String authToken = authHeader.substring(7);
        return messageService.likeMessage(authToken, messageId);
    }
}
