package com.celebritysystems.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
@Entity
@Table(name = "ticket_attachment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TicketAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "file_path", columnDefinition = "LONGBLOB")
    private byte[] filePath;

    private String note;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;

    private LocalDateTime uploadedAt;

    public Long getTicketId() {
        return this.ticket != null ? this.ticket.getId() : null;
    }
}