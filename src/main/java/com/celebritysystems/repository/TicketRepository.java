package com.celebritysystems.repository;

import com.celebritysystems.entity.Ticket;
import com.celebritysystems.entity.enums.TicketStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByCreatedById(Long userId);

    List<Ticket> findByAssignedToWorkerId(Long userId);

    List<Ticket> findByAssignedBySupervisorId(Long userId);

    List<Ticket> findByCompanyId(Long companyId);

    List<Ticket> findByStatus(String status);

    List<Ticket> findByCreatedAtAfter(java.time.LocalDateTime dateTime);

    List<Ticket> findByCompanyIdAndStatus(Long companyId, String status);

    List<Ticket> findByAssignedToWorker_Username(String username);

    long countByStatusIsNullAndCreatedAtAfter(LocalDateTime date);

    long countByAssignedToWorker_Username(String username);

    @Query("SELECT t.status, COUNT(t) FROM Ticket t " +
            "WHERE t.createdAt >= :date AND t.status IS NOT NULL " +
            "GROUP BY t.status")
    List<Object[]> countTicketsGroupByStatusSinceDate(@Param("date") LocalDateTime date);

    long countByAssignedToWorker_UsernameAndStatus(String username, TicketStatus status);

    List<Ticket> findByAssignedToWorkerIsNullAndAssignedBySupervisorIsNull();

    List<Ticket> findByScreenIdInAndCreatedAtBetween(
        List<Long> screenIds,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    List<Ticket> findByCreatedAtBetween(
        LocalDateTime startDate,
        LocalDateTime endDate
    );
}
