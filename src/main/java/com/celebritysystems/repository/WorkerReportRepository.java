package com.celebritysystems.repository;

import com.celebritysystems.entity.WorkerReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkerReportRepository extends JpaRepository<WorkerReport, Long> {
    
    Optional<WorkerReport> findByTicketId(Long ticketId);
    
    @Query("SELECT wr FROM WorkerReport wr WHERE wr.ticket.id = :ticketId")
    Optional<WorkerReport> findByTicket_Id(@Param("ticketId") Long ticketId);
    
    boolean existsByTicketId(Long ticketId);
}