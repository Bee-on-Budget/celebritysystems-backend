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
public class ComponentHistoryDTO {
    private String componentName;
    private Long changeCount;
    private List<ComponentChangeEventDTO> changes;
}