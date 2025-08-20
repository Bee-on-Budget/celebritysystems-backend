package com.celebritysystems.dto.Reports;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentChangesSummaryDTO {
    private String componentName;
    private Long totalChanges;
    private Map<Long, Long> changesPerScreen; 
    private Map<String, Long> changeTypeDistribution; 
}