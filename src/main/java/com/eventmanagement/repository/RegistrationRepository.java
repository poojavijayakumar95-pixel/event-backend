package com.eventmanagement.repository;

import com.eventmanagement.entity.Registration;
import com.eventmanagement.enums.RegistrationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    Optional<Registration> findByUserIdAndEventId(Long userId, Long eventId);

    // FIX Bug 3: check only ACTIVE registrations, not cancelled ones
    boolean existsByUserIdAndEventIdAndStatusIn(Long userId, Long eventId, List<RegistrationStatus> statuses);

    Page<Registration> findByUserId(Long userId, Pageable pageable);

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByEventIdAndStatus(Long eventId, RegistrationStatus status);

    @Query("SELECT COUNT(r) FROM Registration r WHERE r.event.id = :eventId " +
           "AND r.status IN ('REGISTERED', 'ATTENDED')")
    long countActiveRegistrationsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT r FROM Registration r JOIN FETCH r.event e JOIN FETCH r.user u " +
           "WHERE u.id = :userId ORDER BY e.startDateTime ASC")
    List<Registration> findUpcomingByUserId(@Param("userId") Long userId);
}
