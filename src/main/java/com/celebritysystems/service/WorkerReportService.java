package com.celebritysystems.service;

import com.celebritysystems.dto.PatchWorkerReportDTO;
import com.celebritysystems.dto.WorkerReportDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;

public interface WorkerReportService {
    WorkerReportResponseDTO createWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO, WorkerReportDTO.ChecklistData checklistData);
    WorkerReportResponseDTO getWorkerReportByTicketId(Long ticketId);
    WorkerReportResponseDTO updateWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO);
    void deleteWorkerReport(Long ticketId);
    WorkerReportResponseDTO patchWorkerReport(Long ticketId, PatchWorkerReportDTO patchWorkerReportDTO);

}
