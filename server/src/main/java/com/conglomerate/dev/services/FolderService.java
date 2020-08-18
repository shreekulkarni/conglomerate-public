package com.conglomerate.dev.services;

import com.conglomerate.dev.Exceptions.InvalidAuthTokenException;
import com.conglomerate.dev.Exceptions.NoSuchGroupingException;
import com.conglomerate.dev.Exceptions.NotAMemberException;
import com.conglomerate.dev.models.Document;
import com.conglomerate.dev.models.Folder;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.repositories.FolderRepository;
import com.conglomerate.dev.repositories.GroupingRepository;
import com.conglomerate.dev.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FolderService {
    private final FolderRepository folderRepository;
    private final GroupingRepository groupingRepository;
    private final UserRepository userRepository;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public FolderService(FolderRepository folderRepository, GroupingRepository groupingRepository,
                         UserRepository userRepository) {
        this.folderRepository = folderRepository;
        this.groupingRepository = groupingRepository;
        this.userRepository = userRepository;
    }

    public List<Folder> getAllFolders() {
        // this is the logic for the controller endpoint -- it's a simple service so there isn't much logic
        return folderRepository.findAll();
    }

    public int createFolder(String authToken, int groupingId, String folderName) {
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
            throw new NotAMemberException(user.getUserName(), groupingId);
        }

        Folder folder = Folder.builder()
                .grouping(grouping)
                .name(folderName)
                .build();

        folderRepository.save(folder);

        return folder.getId();
    }
}
