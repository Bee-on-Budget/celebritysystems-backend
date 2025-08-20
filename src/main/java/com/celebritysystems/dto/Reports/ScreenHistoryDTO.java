package com.celebritysystems.dto.Reports;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenHistoryDTO {
    private Long screenId;
    private String screenName;
    private List<ComponentHistoryDTO> componentHistories;
    private Long totalChanges;
}