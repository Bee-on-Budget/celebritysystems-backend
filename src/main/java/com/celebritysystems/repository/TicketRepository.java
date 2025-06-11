package com.celebritysystems.repository;

import com.celebritysystems.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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

}
