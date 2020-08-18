package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository  // extends JpaRepository<User, Integer> means it's a repository of Users, and the ids are Integers
public interface UserRepository extends JpaRepository<User, Integer> {
    // this is empty right now, but if you need a Repository method, you'd declare here that you're using it
    List<User> findAllByUserName(String username);
    Optional<User> getByUserName(String username);
    Optional<User> getByAuthTokenHash(String authTokenHash);
    List<User> findAllByEmail(String email);
}

