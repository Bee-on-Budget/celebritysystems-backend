package com.celebritysystems.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatchScreenResolutionDTO {

    private Double resolutionWidth;

    private Double resolutionHeight;
}
