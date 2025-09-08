package com.celebritysystems.service.impl;

import com.celebritysystems.dto.WorkerReportResponseDTO;
import com.celebritysystems.dto.TicketResponseDTO;
import com.celebritysystems.service.PdfService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService {
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    
    @Override
    public Resource generateWorkerReportPdf(WorkerReportResponseDTO workerReport, TicketResponseDTO ticket) {
        String fileName = String.format("worker_report_ticket_%d.pdf", ticket.getId());
        return generateWorkerReportPdf(workerReport, ticket, fileName);
    }
    
    @Override
    public Resource generateWorkerReportPdf(WorkerReportResponseDTO workerReport, TicketResponseDTO ticket, String fileName) {
        log.info("Generating PDF for worker report - Ticket ID: {}, Filename: {}", ticket.getId(), fileName);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add content to PDF
            addTitle(document, ticket);
            addTicketInformation(document, ticket);
            addWorkerReportInformation(document, workerReport);
            addChecklist(document, workerReport);
            addDefectsAndSolutions(document, workerReport);
            addFooter(document);
            
            document.close();
            
            byte[] pdfBytes = baos.toByteArray();
            log.info("PDF generated successfully - Size: {} bytes", pdfBytes.length);
            
            return new ByteArrayResource(pdfBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            
        } catch (Exception e) {
            log.error("Failed to generate PDF for worker report - Ticket ID: {}", ticket.getId(), e);
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }
    
    private void addTitle(Document document, TicketResponseDTO ticket) throws DocumentException {
        Paragraph title = new Paragraph("WORKER REPORT", TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);
        
        Paragraph ticketTitle = new Paragraph("Ticket: " + ticket.getTitle(), HEADER_FONT);
        ticketTitle.setAlignment(Element.ALIGN_CENTER);
        ticketTitle.setSpacingAfter(30f);
        document.add(ticketTitle);
    }
    
    private void addTicketInformation(Document document, TicketResponseDTO ticket) throws DocumentException {
        Paragraph header = new Paragraph("TICKET INFORMATION", HEADER_FONT);
        header.setSpacingBefore(10f);
        header.setSpacingAfter(10f);
        document.add(header);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        
        addTableRow(table, "Ticket ID:", "#" + ticket.getId());
        addTableRow(table, "Title:", ticket.getTitle());
        addTableRow(table, "Description:", ticket.getDescription());
        addTableRow(table, "Status:", ticket.getStatus());
        addTableRow(table, "Company:", ticket.getCompanyName());
        addTableRow(table, "Screen:", ticket.getScreenName());
        addTableRow(table, "Location:", ticket.getLocation());
        addTableRow(table, "Screen Type:", ticket.getScreenType());
        addTableRow(table, "Service Type:", ticket.getServiceTypeDisplayName());
        addTableRow(table, "Assigned Worker:", ticket.getAssignedToWorkerName());
        addTableRow(table, "Created At:", formatDateTime(ticket.getCreatedAt()));
        addTableRow(table, "Resolved At:", formatDateTime(ticket.getResolvedAt()));
        
        document.add(table);
    }
    
    private void addWorkerReportInformation(Document document, WorkerReportResponseDTO workerReport) throws DocumentException {
        Paragraph header = new Paragraph("REPORT INFORMATION", HEADER_FONT);
        header.setSpacingBefore(10f);
        header.setSpacingAfter(10f);
        document.add(header);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        
        addTableRow(table, "Report Date:", formatDateTime(workerReport.getReportDate()));
        addTableRow(table, "Created At:", formatDateTime(workerReport.getCreatedAt()));
        addTableRow(table, "Last Updated:", formatDateTime(workerReport.getUpdatedAt()));
        
        document.add(table);
    }
    
    private void addChecklist(Document document, WorkerReportResponseDTO workerReport) throws DocumentException {
        Paragraph header = new Paragraph("INSPECTION CHECKLIST", HEADER_FONT);
        header.setSpacingBefore(10f);
        header.setSpacingAfter(10f);
        document.add(header);
        
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        
        Map<String, String> checklist = workerReport.getChecklist();
        if (checklist != null && !checklist.isEmpty()) {
            for (Map.Entry<String, String> entry : checklist.entrySet()) {
                String status = entry.getValue() != null ? entry.getValue() : "Not Checked";
                addTableRow(table, entry.getKey() + ":", status);
            }
        } else {
            addTableRow(table, "Checklist:", "No checklist data available");
        }
        
        document.add(table);
    }
    
    private void addDefectsAndSolutions(Document document, WorkerReportResponseDTO workerReport) throws DocumentException {
        // Defects Found Section
        Paragraph defectsHeader = new Paragraph("DEFECTS FOUND", HEADER_FONT);
        defectsHeader.setSpacingBefore(20f);
        defectsHeader.setSpacingAfter(10f);
        document.add(defectsHeader);
        
        Paragraph defectsContent = new Paragraph(
            workerReport.getDefectsFound() != null ? workerReport.getDefectsFound() : "No defects reported", 
            NORMAL_FONT
        );
        defectsContent.setSpacingAfter(20f);
        document.add(defectsContent);
        
        // Solutions Provided Section
        Paragraph solutionsHeader = new Paragraph("SOLUTIONS PROVIDED", HEADER_FONT);
        solutionsHeader.setSpacingBefore(10f);
        solutionsHeader.setSpacingAfter(10f);
        document.add(solutionsHeader);
        
        Paragraph solutionsContent = new Paragraph(
            workerReport.getSolutionsProvided() != null ? workerReport.getSolutionsProvided() : "No solutions reported", 
            NORMAL_FONT
        );
        solutionsContent.setSpacingAfter(20f);
        document.add(solutionsContent);
    }
    
    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("This report was generated automatically by Celebrity Systems", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(30f);
        document.add(footer);
        
        Paragraph timestamp = new Paragraph("Generated on: " + java.time.LocalDateTime.now().format(DATE_FORMAT), SMALL_FONT);
        timestamp.setAlignment(Element.ALIGN_CENTER);
        document.add(timestamp);
    }
    
    private void addTableRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, HEADER_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);
        labelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "N/A", NORMAL_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5f);
        
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
    
    private String formatDateTime(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMAT) : "N/A";
    }
}