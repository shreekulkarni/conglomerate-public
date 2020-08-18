package com.conglomerate.dev.services;

import com.conglomerate.dev.Exceptions.*;
import com.conglomerate.dev.models.Document;
import com.conglomerate.dev.models.Folder;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import com.conglomerate.dev.models.domain.ListFolderDomain;
import com.conglomerate.dev.models.domain.ListGroupDocsDomain;
import com.conglomerate.dev.repositories.DocumentRepository;
import com.conglomerate.dev.repositories.FolderRepository;
import com.conglomerate.dev.repositories.GroupingRepository;
import com.conglomerate.dev.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentService {
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final GroupingRepository groupingRepository;
    private final S3Service s3Service;
    private final FolderRepository folderRepository;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public DocumentService(DocumentRepository documentRepository, UserRepository userRepository,
                           GroupingRepository groupingRepository, S3Service s3Service, FolderRepository folderRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.groupingRepository = groupingRepository;
        this.s3Service = s3Service;
        this.folderRepository = folderRepository;
    }

    public List<Document> getAllDocuments() {
        // this is the logic for the controller endpoint -- it's a simple service so there isn't much logic
        return documentRepository.findAll();
    }

    public int upload(String authToken, int groupingId, String fileName, byte[] fileData, boolean shared) {
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

        Document document = Document.builder()
                .name(fileName)
                .grouping(maybeGrouping.get())
                .uploader(user)
                .uploadDate(LocalDateTime.now())
                .documentLink("temp")
                .shared(shared)
                .build();

        documentRepository.save(document);

        try {
            document.setDocumentLink(s3Service.uploadFile(groupingId + "/" + document.getId() + "/" + fileName, fileData));
            documentRepository.save(document);

            return document.getId();
        } catch (IOException e) {
            documentRepository.delete(document);
            throw new DocumentUploadException();
        }
    }

    public int uploadToFolder(String authToken, int groupingId, int folderId, String fileName, byte[] fileData, boolean shared) {
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

        System.out.println("Looking for folder: " + folderId);
        Optional<Folder> maybeFolder = folderRepository.findById(folderId);
        if (!maybeFolder.isPresent()) {
            throw new NoSuchFolderException(folderId);
        }
        Folder folder = maybeFolder.get();

        Document document = Document.builder()
                .name(fileName)
                .grouping(maybeGrouping.get())
                .folder(folder)
                .uploader(user)
                .uploadDate(LocalDateTime.now())
                .documentLink("temp")
                .shared(shared)
                .build();

        documentRepository.save(document);

        try {
            document.setDocumentLink(s3Service.uploadFile(groupingId + "/" +
                    "folder-" + folderId + "/" +
                    document.getId() + "/"  +
                    fileName,
                    fileData));
            documentRepository.save(document);

            return document.getId();
        } catch (IOException e) {
            documentRepository.delete(document);
            throw new DocumentUploadException();
        }
    }

    public List<ListGroupDocsDomain> listDocs(String authToken, int groupingId) {
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

        List<Document> documents = documentRepository.findByGrouping(grouping);
        List<ListDocumentDomain> documentDomain = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getFolder() == null &&
                    (documents.get(i).isShared() || documents.get(i).getUploader().equals(user))) {
                documentDomain.add(ListDocumentDomain.createDomain(documents.get(i)));
            }
        }

        List<Folder> folders = folderRepository.findByGrouping(grouping);
        List<ListFolderDomain> folderDomain = new ArrayList<>();

        for (int i = 0; i < folders.size(); i++) {
            ListFolderDomain listFolderDomain = ListFolderDomain.builder()
                    .id(folders.get(i).getId())
                    .groupingId(folders.get(i).getGrouping().getId())
                    .name(folders.get(i).getName())
                    .documents(ListDocumentDomain.createList(folders.get(i).getDocuments()))
                    .build();

            folderDomain.add(listFolderDomain);
        }
        ListGroupDocsDomain listGroupDocsDomain = new ListGroupDocsDomain(documentDomain, folderDomain);

        List<ListGroupDocsDomain> list = new ArrayList<>();
        list.add(listGroupDocsDomain);

        return list;
    }


    public void delete(String authToken, int documentId) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Document> maybeDocument = documentRepository.findById(documentId);
        if (!maybeDocument.isPresent()) {
            throw new NoSuchDocumentException(documentId);
        }
        Document document = maybeDocument.get();

        if (document.getUploader() == null || !document.getUploader().equals(user)) {
            throw new NotDocumentOwnerException(user.getUserName(), documentId);
        }

        String s3key = document.getGrouping().getId() + "/" +
                ((document.getFolder() == null) ? "" : "folder-" + document.getFolder().getId() + "/") +
                documentId + "/" +
                document.getName();
        s3Service.deleteFile(s3key);

        documentRepository.delete(document);
    }

    public void setShared(String authToken, int documentId, boolean shared) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Document> maybeDocument = documentRepository.findById(documentId);
        if (!maybeDocument.isPresent()) {
            throw new NoSuchDocumentException(documentId);
        }
        Document document = maybeDocument.get();

        if (document.getUploader() == null || !document.getUploader().equals(user)) {
            throw new NotDocumentOwnerException(user.getUserName(), documentId);
        }

        document.setShared(shared);
        documentRepository.save(document);
    }

    public List<ListDocumentDomain> searchForDocument(String authToken, int groupingId, String searchString) {
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

        List<Document> documents = documentRepository.findByGrouping(grouping);
        List<ListDocumentDomain> listDocumentDomain = new ArrayList<>();

        for (int i = 0; i < documents.size(); i++) {
            if (documents.get(i).getName().startsWith(searchString) && documents.get(i).isShared()) {
                listDocumentDomain.add(ListDocumentDomain.createDomain(documents.get(i)));
            }
        }

        return listDocumentDomain;

    }

    /*
    public int setViewPermissions(String authToken, int documentId, boolean uploaderOnly) {
        String authTokenHash = UserService.hash(authToken);
        Optional<User> maybeUser = userRepository.getByAuthTokenHash(authTokenHash);
        if (!maybeUser.isPresent()) {
            throw new InvalidAuthTokenException();
        }
        User user = maybeUser.get();

        Optional<Document> maybeDocument = documentRepository.findById(documentId);
        if (!maybeDocument.isPresent()) {
            throw new NoSuchDocumentException(documentId);
        }
        Document document = maybeDocument.get();

        Grouping grouping = document.getGrouping();

        if (!grouping.getMembers().contains(user)) {
            throw new NotAMemberException(user.getUserName(), grouping.getId());
        }

        if (!document.getUploader().equals(user)) {
            throw new NotDocumentOwnerException(user.getUserName(), documentId);
        }

        int returnValue;
        Set<User> usersAllowed = new HashSet<>();
        if (uploaderOnly) {
            usersAllowed.add(document.getUploader());
            returnValue = 0;
        }
        else {
            usersAllowed.add((User) document.getGrouping().getMembers());
            returnValue = 1;
        }

        document.setViewPermissions(usersAllowed);
        documentRepository.save(document);

        return returnValue;
    }
    */
}
