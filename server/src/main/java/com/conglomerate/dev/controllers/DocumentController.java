package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Document;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import com.conglomerate.dev.models.domain.ListDocumentDomain;
import com.conglomerate.dev.models.domain.ListGroupDocsDomain;
import com.conglomerate.dev.services.DocumentService;
import com.conglomerate.dev.services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
// the server will look to map requests that start with "/documents" to the endpoints in this controller
@RequestMapping(value = "/documents")
public class DocumentController {
    private final DocumentService documentService;

    @Autowired
    private S3Service s3Service;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // @GetMapping maps HTTP GET requests on the endpoint to this method
    // Because no url value has been specified, this is mapping the class-wide "/documents" url
    @GetMapping(produces = "application/json; charset=utf-8")
    public List<Document> getAllDocuments() {
        // Have the logic in GroupService
        // Ideally, GroupController should just control the request mappings
        return documentService.getAllDocuments();
    }


    @GetMapping(value = "/test")
    public void testUpload() {
        File uploadFile = new File("C:/Users/lukel/Downloads/hw1.pdf");
        System.out.println(uploadFile.getName());
        s3Service.uploadFile(uploadFile);
    }

    @PostMapping("/{groupingId}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public int uploadDocument(@RequestHeader("authorization") String authHeader, @PathVariable int groupingId,
                               @RequestPart(value = "file") MultipartFile multipartFile,
                              @RequestPart(value = "shared") String shared) throws IOException {
        String authToken = authHeader.substring(7);
        boolean sharedBool = shared.equalsIgnoreCase("true");

        return documentService.upload(authToken, groupingId,
                multipartFile.getOriginalFilename(), multipartFile.getBytes(), sharedBool);
    }

    @PostMapping("/{groupingId}/{folderId}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public int uploadDocumentFolder(@RequestHeader("authorization") String authHeader, @PathVariable int groupingId,
                                    @PathVariable int folderId,
                                    @RequestPart(value = "file") MultipartFile multipartFile,
                                    @RequestPart(value = "shared") String shared) throws IOException {
        String authToken = authHeader.substring(7);
        boolean sharedBool = shared.equalsIgnoreCase("true");

        return documentService.uploadToFolder(authToken, groupingId, folderId,
                multipartFile.getOriginalFilename(), multipartFile.getBytes(), sharedBool);
    }

    @GetMapping(value = "/{groupingId}/list-docs", produces = "application/json; charset=utf-8")
    public List<ListGroupDocsDomain> listDocs(@RequestHeader("authorization") String authHeader, @PathVariable int groupingId) {
        String authToken = authHeader.substring(7);

        return documentService.listDocs(authToken, groupingId);
    }

    @DeleteMapping(value = "/{documentId}")
    public void deleteDocument(@RequestHeader("authorization") String authHeader, @PathVariable int documentId) {
        String authToken = authHeader.substring(7);

        documentService.delete(authToken, documentId);
    }

    @PostMapping(value = "/{documentId}/set-shared")
    public void setShared(@RequestHeader("authorization") String authHeader, @PathVariable int documentId,
                            @RequestBody boolean shared) {
        String authToken = authHeader.substring(7);

        documentService.setShared(authToken, documentId, shared);
    }

    @GetMapping(value = "/{groupingId}/search-docs/{searchString}", produces = "application/json; charset=utf-8")
    public List<ListDocumentDomain> searchForDocument(@RequestHeader("authorization") String authHeader,
                                                      @PathVariable int groupingId, @PathVariable String searchString) {
        String authToken = authHeader.substring(7);

        return documentService.searchForDocument(authToken, groupingId, searchString);
    }

    /*
    @PostMapping("/{documentId}/set-permissions")
    public int setViewPermissions(@RequestHeader("authorization") String authHeader,
                                        @PathVariable int documentId, @RequestBody boolean uploaderOnly) {
        String authToken = authHeader.substring(7);

        return documentService.setViewPermissions(authToken, documentId, uploaderOnly);
    }
    */
}
