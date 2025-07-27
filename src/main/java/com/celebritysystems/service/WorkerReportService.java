package com.celebritysystems.service;

import com.celebritysystems.dto.WorkerReportDTO;
import com.celebritysystems.dto.WorkerReportResponseDTO;

public interface WorkerReportService {
    WorkerReportResponseDTO createWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO);
    WorkerReportResponseDTO getWorkerReportByTicketId(Long ticketId);
    WorkerReportResponseDTO updateWorkerReport(Long ticketId, WorkerReportDTO workerReportDTO);
    void deleteWorkerReport(Long ticketId);
}