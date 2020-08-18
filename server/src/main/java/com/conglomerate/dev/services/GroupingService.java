package com.conglomerate.dev.services;

import com.conglomerate.dev.Exceptions.*;
import com.conglomerate.dev.models.Document;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.Message;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.GetMessageDomain;
import com.conglomerate.dev.models.domain.RemoveMemberDomain;
import com.conglomerate.dev.models.domain.UpdateGroupingNameDomain;
import com.conglomerate.dev.Exceptions.InvalidAuthTokenException;
import com.conglomerate.dev.repositories.DocumentRepository;
import com.conglomerate.dev.repositories.GroupingRepository;
import com.conglomerate.dev.repositories.MessageRepository;
import com.conglomerate.dev.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupingService {
    private final GroupingRepository groupingRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final DocumentRepository documentRepository;
    private final DocumentService documentService;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public GroupingService(GroupingRepository groupingRepository, UserRepository userRepository,
                           MessageRepository messageRepository, DocumentRepository documentRepository,
                           DocumentService documentService) {
        this.groupingRepository = groupingRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.documentRepository = documentRepository;
        this.documentService = documentService;
    }

    public List<Grouping> getAllGroupings() {
        // this is the logic for the controller endpoint -- it's a simple service so there isn't much logic
        return groupingRepository.findAll();
    }

    public List<Grouping> getUsersGroupings(String authToken) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        return groupingRepository.findByMembersContains(user);
    }

    public int addGrouping(String authToken, String groupName) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeOwner = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeOwner.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        User owner = maybeOwner.get();

        HashSet<User> members = new HashSet<User>();
        members.add(owner);

        Grouping toAdd = Grouping.builder()
                .name(groupName)
                .owner(owner)
                .creationDate(LocalDateTime.now())
                .members(members)
                .build();

        toAdd = groupingRepository.save(toAdd);

        return toAdd.getId();
    }

    public String updateGroupingName(String authToken, UpdateGroupingNameDomain updateGroupingNameDomain) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeOwner = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeOwner.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        User owner = maybeOwner.get();

        Optional<Grouping> maybeGrouping = groupingRepository.findById(updateGroupingNameDomain.getGroupingId());

        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(updateGroupingNameDomain.getGroupingId());
        }

        Grouping grouping = maybeGrouping.get();

        List<Grouping> ownersGroupings = groupingRepository.findByOwner(owner);

        if (!ownersGroupings.contains(grouping)) {
            throw new NotGroupOwnerException(owner.getUserName());
        }

        grouping.setName(updateGroupingNameDomain.getNewGroupName());

        grouping = groupingRepository.save(grouping);

        return grouping.getName();
    }

    public int joinGrouping(String authToken, int groupingId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        User user = maybeUser.get();

        Optional<Grouping> maybeGrouping = groupingRepository.findById(groupingId);
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(groupingId);
        }

        Grouping grouping = maybeGrouping.get();

        Set<User> members = grouping.getMembers();
        if (members.contains(user)) {
            throw new UserAlreadyInGroupingException(user.getUserName());
        }

        grouping.getMembers().add(user);

        grouping = groupingRepository.save(grouping);

        return grouping.getId();
    }

    public int leaveGrouping(String authToken, int groupingId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }

        User user = maybeUser.get();

        Optional<Grouping> maybeGrouping = groupingRepository.findById(groupingId);
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(groupingId);
        }

        Grouping grouping = maybeGrouping.get();

        Set<User> members = grouping.getMembers();
        if (!members.contains(user)) {
            throw new UserNotInGroupingException(user.getUserName(), grouping.getId());
        }

        grouping.getMembers().remove(user);

        // remove the user's documents for this group
        List<Document> documents = documentRepository.findByUploaderAndGrouping(user, grouping);
        for (Document doc : documents) {
            if (!doc.isShared()) {
                documentService.delete(authToken, doc.getId());
            } else {
                doc.setUploader(null);
                documentRepository.save(doc);
            }
        }

        if ((grouping.getMembers().size() >= 1) && (grouping.getOwner().equals(user))) {
            Set<User> currentMembers = grouping.getMembers();

            Iterator<User> membersIterator = currentMembers.iterator();

            grouping.setOwner(membersIterator.next());
        }

        if (grouping.getMembers().size() == 0) {
            groupingRepository.delete(grouping);

            return 0;
        }

        grouping = groupingRepository.save(grouping);

        return grouping.getId();
    }
  
    public List<GetMessageDomain> getLatestMessages(String authToken, int groupingId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Grouping> maybeGrouping = groupingRepository.findById(groupingId);
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(groupingId);
        }
        Grouping grouping = maybeGrouping.get();

        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), grouping.getId());
        }

        List<Message> messages = messageRepository.findTop30MessagesByGroupingOrderByTimestampDesc(grouping);
        List<GetMessageDomain> response = messages
                .stream()
                .map(GetMessageDomain::fromMessage)
                .collect(Collectors.toList());

        // MARK THE REQUESTING USER AS READING THE RETRIEVED MESSAGES (after constructing response)
        for (Message m : messages) {
            m.getRead().add(user);
            messageRepository.save(m);
        }

        return response;
    }

    public String removeMember(String authToken, RemoveMemberDomain removeMemberDomain) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeOwner = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeOwner.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User owner = maybeOwner.get();
        if (owner.getUserName().equals(removeMemberDomain.getUsername()))  {
            leaveGrouping(authToken, removeMemberDomain.getGroupingId());
            return owner.getUserName();
        }

        Optional<Grouping> maybeGrouping = groupingRepository.findById(removeMemberDomain.getGroupingId());
        if (!maybeGrouping.isPresent()) {
            throw new NoSuchGroupingException(removeMemberDomain.getGroupingId());
        }

        Grouping grouping = maybeGrouping.get();

        if (!grouping.getOwner().equals(owner)) {
            throw new NotGroupOwnerException(owner.getUserName());
        }

        Set<User> members = grouping.getMembers();
        Iterator<User> membersList = members.iterator();
        boolean found = false;
        User memberToRemove = null;

        while (membersList.hasNext()) {
            memberToRemove = membersList.next();
            if (memberToRemove.getUserName().equals(removeMemberDomain.getUsername())) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new UserNotInGroupingException(removeMemberDomain.getUsername(), removeMemberDomain.getGroupingId());
        }

        grouping.getMembers().remove(memberToRemove);

        grouping = groupingRepository.save(grouping);

        return memberToRemove.getUserName();
    }
  
}