package com.celebritysystems.service;

import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import org.springframework.core.io.Resource;

public interface PdfService {
    
    /**
     * Generate a PDF report for a worker report
     * 
     * @param workerReport The worker report data
     * @param ticket The ticket data associated with the report
     * @return Resource containing the generated PDF
     */
    Resource generateWorkerReportPdf(WorkerReportResponseDTO workerReport, TicketResponseDTO ticket);
    
    /**
     * Generate a PDF report and save it to a temporary location
     * 
     * @param workerReport The worker report data
     * @param ticket The ticket data associated with the report
     * @param fileName The desired filename for the PDF
     * @return Resource containing the generated PDF
     */
    Resource generateWorkerReportPdf(WorkerReportResponseDTO workerReport, TicketResponseDTO ticket, String fileName);
}