package com.celebritysystems.dto;

import com.celebritysystems.entity.enums.ScreenType;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateScreenRequestDto {
    private String name;
    private ScreenType screenType;
    private SolutionTypeInScreen solutionTypeInScreen;
    private String location;

    //    private String pixelScreen;
    private String batchScreen;

    private double screenWidth;
    private double screenHeight;

    private double resolutionWidth;
    private double resolutionHeight;

    private double pixelPitchWidth;
    private double pixelPitchHeight;

    private String description;

    private String powerSupply;

    private Long powerSupplyQuantity;

    private Long sparePowerSupplyQuantity;

    private String receivingCard;

    private Long receivingCardQuantity;

    private Long spareReceivingCardQuantity;

    //power
    private String mainPowerCable;
    private Long mainPowerCableQuantity;
    private Long spareMainPowerCableQuantity;

    private String loopPowerCable;
    private Long loopPowerCableQuantity;
    private Long spareLoopPowerCableQuantity;

    //data
    private String mainDataCable;
    private Long mainDataCableQuantity;
    private Long spareMainDataCableQuantity;

    private String loopDataCable;
    private Long loopDataCableQuantity;
    private Long spareLoopDataCableQuantity;

    private String media;
    private Long mediaQuantity;
    private Long spareMediaQuantity;

    private String hub;
    private Long hubQuantity;
    private Long spareHubQuantity;

    private String fan;
    private Long fanQuantity;

    private MultipartFile connectionFile;
    private MultipartFile configFile;
    private MultipartFile versionFile;

    //    private String moduleBatchNumber;
//    private Long moduleQuantity;
//    private Double moduleHeight;
//    private Double moduleWidth;
    private String moduleDtoListJson; // Will contain the list as JSON string


    //    private List<CabinDto> cabinDtoList;
    private String cabinDtoListJson; // Will contain the list as JSON string

}
