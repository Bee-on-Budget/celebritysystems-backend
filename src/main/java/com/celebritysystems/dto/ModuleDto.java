package com.celebritysystems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDto {
    private String moduleBatchNumber;
    private Long quantity;

    private Long heightQuantity;
    private Long widthQuantity;

    private Double height;
    private Double width;

    private Boolean isHeight = false;
    private Boolean isWidth = false;
}
