package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.Grouping;
import com.conglomerate.dev.models.Message;
import com.conglomerate.dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findBySender(User user);
    List<Message> findMessagesByGrouping(Grouping grouping);
    List<Message> findTop30MessagesByGroupingOrderByTimestampDesc(Grouping grouping);
    Optional<Message> findById(Integer messageId);
    List<Message> findByReadContains(User user);
}
