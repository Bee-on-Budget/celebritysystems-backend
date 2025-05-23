package com.celebritysystems.dto;

import com.celebritysystems.entity.Screen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModuleDto {
    private Long quantity;

    private Double height;

    private Double width;
}
