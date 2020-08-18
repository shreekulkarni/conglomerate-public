package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.Folder;
import com.conglomerate.dev.models.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Integer>  {
    List<Folder> findByGrouping(Grouping grouping);
}
