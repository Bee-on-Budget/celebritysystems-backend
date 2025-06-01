package com.celebritysystems.dto;

import com.celebritysystems.entity.Module;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CabinDto {
    private String cabinetName;
    private Long quantity;

    private Double height;

    private Double width;
    private ModuleDto moduleDto;
}
