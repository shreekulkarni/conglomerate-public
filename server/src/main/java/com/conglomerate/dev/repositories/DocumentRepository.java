package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.Document;
import com.conglomerate.dev.models.Folder;
import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    List<Document> findByGrouping(Grouping grouping);
    List<Document> findByFolder(Folder folder);
    List<Document> findByUploader(User uploader);
    List<Document> findByUploaderAndGrouping(User uploader, Grouping grouping);
}
