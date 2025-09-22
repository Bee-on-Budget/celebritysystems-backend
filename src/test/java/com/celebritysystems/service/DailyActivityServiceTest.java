package com.celebritysystems.service;

import com.celebritysystems.dto.statistics.DailyActivityResponseDTO;
import com.celebritysystems.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DailyActivityServiceTest {

    @Autowired
    private DailyActivityService dailyActivityService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Test
    public void testGetDailyActivityStats() {
        // Test with a date range
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        
        DailyActivityResponseDTO result = dailyActivityService.getDailyActivityStats(startDate, endDate);
        
        // Verify the response structure
        assertNotNull(result);
        assertEquals(startDate, result.getStartDate());
        assertEquals(endDate, result.getEndDate());
        assertNotNull(result.getDailyStats());
        
        // Verify that we have stats for each day in the range
        long expectedDays = startDate.until(endDate).getDays() + 1;
        assertEquals(expectedDays, result.getDailyStats().size());
        
        // Verify that totals are non-negative
        assertTrue(result.getTotalUsersCreated() >= 0);
        assertTrue(result.getTotalContractsCreated() >= 0);
        assertTrue(result.getTotalTicketsCreated() >= 0);
        assertTrue(result.getTotalCompaniesCreated() >= 0);
        assertTrue(result.getTotalActivity() >= 0);
        
        // Verify that total activity equals sum of individual activities
        long expectedTotal = result.getTotalUsersCreated() + 
                           result.getTotalContractsCreated() + 
                           result.getTotalTicketsCreated() + 
                           result.getTotalCompaniesCreated();
        assertEquals(expectedTotal, result.getTotalActivity());
    }
}

