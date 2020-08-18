package com.conglomerate.dev.repositories;

import com.conglomerate.dev.models.Event;
import com.conglomerate.dev.models.Grouping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    List<Event> findByGrouping(Grouping grouping);
    List<Event> findByDateTimeBefore(LocalDateTime localDateTime);
}
