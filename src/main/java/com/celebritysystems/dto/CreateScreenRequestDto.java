package com.celebritysystems.dto;

import com.celebritysystems.entity.enums.ScreenType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateScreenRequestDto {
    private String name;
    private ScreenType screenType;
    private String location;
    private Double height;

    private Double width;

    private String powerSupply;

    private Long powerSupplyQuantity;

    private Long sparePowerSupplyQuantity;

    private String receivingCard;

    private Long receivingCardQuantity;

    private Long spareReceivingCardQuantity;

    private String cable;

    private Long cableQuantity;

    private Long spareCableQuantity;

    private String powerCable;

    private Long powerCableQuantity;

    private Long sparePowerCableQuantity;

    private String dataCable;

    private Long dataCableQuantity;

    private Long spareDataCableQuantity;

    private MultipartFile connectionFile;
    private MultipartFile configFile;
    private MultipartFile versionFile;

    private Long moduleId;
    private Long cabinId;

//    private ModuleDto module;
//    private CabinDto cabin;


}
