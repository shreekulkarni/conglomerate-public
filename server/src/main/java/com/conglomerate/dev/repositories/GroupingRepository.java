package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository  // extends JpaRepository<Group, Integer> means it's a repository of Groups, and the ids are Integers
public interface GroupingRepository extends JpaRepository<Grouping, Integer> {
    List<Grouping> findByMembersContains(User user);
    List<Grouping> findByOwner(User user);
    Optional<Grouping> findById(Integer id);
}