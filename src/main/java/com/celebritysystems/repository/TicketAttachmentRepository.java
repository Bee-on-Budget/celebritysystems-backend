package com.celebritysystems.repository;

import com.celebritysystems.entity.TicketAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketAttachmentRepository extends JpaRepository<TicketAttachment, Long> {
    List<TicketAttachment> findByTicket_Id(Long ticketId); 

    List<TicketAttachment> findByUploadedById(Long userId);

    List<TicketAttachment> findByUploadedAtAfter(java.time.LocalDateTime time);
}
