package com.conglomerate.dev.controllers;

import com.conglomerate.dev.models.Document;
import com.conglomerate.dev.models.Folder;
import com.conglomerate.dev.services.DocumentService;
import com.conglomerate.dev.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
// the server will look to map requests that start with "/documents" to the endpoints in this controller
@RequestMapping(value = "/folders")
public class FolderController {
    private final FolderService folderService;

    // this autowired annotation is magic that will link the correct repository into this constructor to make the service
    @Autowired
    public FolderController(FolderService folderService) {
        this.folderService = folderService;
    }

    // @GetMapping maps HTTP GET requests on the endpoint to this method
    // Because no url value has been specified, this is mapping the class-wide "/folders" url
    @GetMapping(produces = "application/json; charset=utf-8")
    public List<Folder> getAllFolders() {
        // Have the logic in FolderService
        // Ideally, FolderController should just control the request mappings
        return folderService.getAllFolders();
    }

    @PostMapping("/{groupingId}/create")
    @ResponseStatus(HttpStatus.CREATED)
    public int createFolder(@RequestHeader("authorization") String authHeader,
                            @PathVariable int groupingId, @RequestBody String folderName) {

        String authToken = authHeader.substring(7);

        return folderService.createFolder(authToken, groupingId, folderName);
    }
}
