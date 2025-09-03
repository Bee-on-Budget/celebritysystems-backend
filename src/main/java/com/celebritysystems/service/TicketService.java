package com.celebritysystems.service;

import com.celebritysystems.dto.TicketDTO;
import com.celebritysystems.dto.CreateTicketDTO;
import com.celebritysystems.dto.PatchTicketDTO;
import com.celebritysystems.dto.TicketAnalyticsDTO;
import com.celebritysystems.dto.TicketAnalyticsSummaryDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

public interface TicketService {
    List<TicketResponseDTO> getAllTickets();

    TicketResponseDTO getTicketById(Long id);

    TicketDTO createTicket(CreateTicketDTO ticketDTO);

    TicketDTO updateTicket(Long id, CreateTicketDTO updatedTicketDTO);

    void deleteTicket(Long id);

    List<TicketResponseDTO> getTicketsByWorkerName(String workerName);

    long countTicketsAssignedToWorker(String username);

Page<TicketResponseDTO> getAllTicketsPaginated(int page, int size, String status, Long companyId, 
                                              Long screenId, Long assignedToWorkerId, String serviceType, Boolean pending);
    long countTicketsCompletedByWorker(String username);

    Long getTicketsCount();

    Map<String, Long> getTicketCountByStatus();

    List<TicketResponseDTO> getPendingTickets();

    TicketDTO patchTicket(Long id,PatchTicketDTO patchTicketDTO);

    List<TicketResponseDTO> getTicketsByCompanyId(Long companyId);
     List<TicketAnalyticsDTO> getTicketAnalytics(List<Long> screenIds, 
                                               LocalDate startDate, 
                                               LocalDate endDate);
    
    List<TicketResponseDTO> getTicketsWithWorkerReportsByScreenId(Long screenId);

    TicketAnalyticsSummaryDTO getTicketAnalyticsSummary(List<Long> screenIds, 
                                                        LocalDate startDate, 
                                                        LocalDate endDate);              
}
