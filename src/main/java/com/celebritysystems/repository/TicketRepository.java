package com.celebritysystems.repository;

import com.celebritysystems.entity.Ticket;
import com.celebritysystems.entity.enums.ServiceType;
import com.celebritysystems.entity.enums.TicketStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    List<Ticket> findByScreenId(Long screenId);

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
            LocalDateTime endDate);

    List<Ticket> findByCreatedAtBetween(
            LocalDateTime startDate,
            LocalDateTime endDate);


               @Query("SELECT t FROM Ticket t WHERE t.assignedToWorker IS NULL AND t.assignedBySupervisor IS NULL")
    Page<Ticket> findPendingTicketsPaginated(Pageable pageable);

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:companyId IS NULL OR t.company.id = :companyId) AND " +
           "(:screenId IS NULL OR t.screen.id = :screenId) AND " +
           "(:assignedToWorkerId IS NULL OR t.assignedToWorker.id = :assignedToWorkerId) AND " +
           "(:serviceType IS NULL OR t.serviceType = :serviceType)")
    Page<Ticket> findTicketsWithFilters(
            @Param("status") TicketStatus status,
            @Param("companyId") Long companyId,
            @Param("screenId") Long screenId,
            @Param("assignedToWorkerId") Long assignedToWorkerId,
            @Param("serviceType") ServiceType serviceType,
            Pageable pageable);

    @Query(value = "SELECT DATE(created_at) as date, COUNT(*) as count " +
                   "FROM ticket " +
                   "WHERE created_at BETWEEN :startDate AND :endDate " +
                   "GROUP BY DATE(created_at) " +
                   "ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyTicketCreationStats(@Param("startDate") LocalDateTime startDate, 
                                             @Param("endDate") LocalDateTime endDate);
}
