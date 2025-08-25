package com.celebritysystems.repository;

import com.celebritysystems.entity.WorkerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkerReportRepository extends JpaRepository<WorkerReport, Long> {
    
    Optional<WorkerReport> findByTicketId(Long ticketId);
    
    @Query("SELECT wr FROM WorkerReport wr WHERE wr.ticket.id = :ticketId")
    Optional<WorkerReport> findByTicket_Id(@Param("ticketId") Long ticketId);
    
    boolean existsByTicketId(Long ticketId);

     List<WorkerReport> findByReportDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<WorkerReport> findByTicketIdInAndReportDateBetween(
        List<Long> ticketIds, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    @Query("SELECT wr FROM WorkerReport wr JOIN wr.ticket t WHERE t.id IN :screenIds AND wr.reportDate BETWEEN :startDate AND :endDate ORDER BY wr.reportDate ASC")
    List<WorkerReport> findByScreenIdsAndDateRange(
        @Param("screenIds") List<Long> screenIds,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT wr FROM WorkerReport wr WHERE wr.reportDate BETWEEN :startDate AND :endDate ORDER BY wr.reportDate ASC")
    List<WorkerReport> findAllByDateRange(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT COUNT(wr) FROM WorkerReport wr JOIN wr.ticket t WHERE " +
           "(:screenIds IS NULL OR t.id IN :screenIds) AND " +
           "wr.reportDate BETWEEN :startDate AND :endDate")
    Long countReportsByScreenIdsAndDateRange(
        @Param("screenIds") List<Long> screenIds,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    @Query("SELECT wr FROM WorkerReport wr WHERE wr.ticket.screen.id IN :screenIds AND wr.reportDate BETWEEN :startDate AND :endDate")
List<WorkerReport> findByScreenIdInAndReportDateBetween(
    @Param("screenIds") List<Long> screenIds,
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate
);
List<WorkerReport> findByTicketScreenIdInAndReportDateBetween(
    List<Long> screenIds, 
    LocalDateTime startDate, 
    LocalDateTime endDate
);
}
