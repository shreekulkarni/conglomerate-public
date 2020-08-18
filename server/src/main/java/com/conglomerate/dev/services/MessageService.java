package com.conglomerate.dev.services;

import com.conglomerate.dev.Exceptions.*;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.Message;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.SendMessageDomain;
import com.conglomerate.dev.repositories.GroupingRepository;
import com.conglomerate.dev.repositories.MessageRepository;
import com.conglomerate.dev.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final UserRepository userRepository;
    private final GroupingRepository groupingRepository;
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(UserRepository userRepository,
                          GroupingRepository groupingRepository,
                          MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.groupingRepository = groupingRepository;
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public int sendMessage(String authToken, SendMessageDomain sendMessageDomain) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        Optional<Grouping> maybeGrouping = groupingRepository.findById(sendMessageDomain.getGroupingId());
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(sendMessageDomain.getGroupingId());
        }

        User user = maybeUser.get();
        Grouping grouping = maybeGrouping.get();
        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), grouping.getId());
        }

        Message toSend = Message.builder()
                                .content(sendMessageDomain.getContent())
                                .timestamp(LocalDateTime.now())
                                .sender(user)
                                .grouping(grouping)
                                .build();

        messageRepository.save(toSend);

        return toSend.getId();
    }

    public int likeMessage(String authToken, int messageId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Message> maybeMessage = messageRepository.findById(messageId);
        if (!maybeMessage.isPresent()) {
            throw new NoSuchMessageException(messageId);
        }
        Message message = maybeMessage.get();

        Grouping grouping = message.getGrouping();
        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), grouping.getId());
        }

        message.setLikes(message.getLikes() + 1);

        messageRepository.save(message);

        return message.getLikes();
    }
}
