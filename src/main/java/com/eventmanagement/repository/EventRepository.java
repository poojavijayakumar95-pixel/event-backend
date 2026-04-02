package com.eventmanagement.repository;

import com.eventmanagement.entity.Event;
import com.eventmanagement.enums.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByIsActiveTrue(Pageable pageable);

    Page<Event> findByIsActiveTrueAndCategory(EventCategory category, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.isActive = true " +
           "AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:category IS NULL OR e.category = :category) " +
           "AND (:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
           "AND (:startDate IS NULL OR e.startDateTime >= :startDate) " +
           "AND (:endDate IS NULL OR e.startDateTime <= :endDate)")
    Page<Event> searchEvents(
            @Param("search") String search,
            @Param("category") EventCategory category,
            @Param("location") String location,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT e FROM Event e WHERE e.isActive = true AND e.startDateTime BETWEEN :start AND :end")
    List<Event> findUpcomingEventsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(e) FROM Event e WHERE e.isActive = true")
    long countActiveEvents();
}
