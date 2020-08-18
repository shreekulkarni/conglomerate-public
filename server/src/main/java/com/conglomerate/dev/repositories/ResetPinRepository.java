package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.ResetPin;
import com.conglomerate.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResetPinRepository extends JpaRepository<ResetPin, Integer> {
    Optional<ResetPin> findByHashedPinAndUser(String hashedPin, User user);
    List<ResetPin> findByExpirationBefore(LocalDateTime localDateTime);
}
