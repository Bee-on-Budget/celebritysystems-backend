package com.celebritysystems.dto;

import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.ScreenType;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScreenResponse {
    private Long id;

    private String name;

    private ScreenType screenType;

    private String location;

    private SolutionTypeInScreen solutionType;

    private Double pixelPitchWidth;
    private Double pixelPitchHeight;

    private Double screenWidth;
    private Double screenHeight;

//    private String pixelScreen;

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

    private String fan;
    private Long fanQuantity;

    private String hub;

    private Long hubQuantity;

    private Long spareHubQuantity;
    private String resolution;

    private LocalDateTime createdAt;

    private List<Module> moduleList;

    private List<Cabin> cabinList;

    // File-related fields
    private String connectionFileUrl;
    private String connectionFileName;
    private String configFileUrl;
    private String configFileName;
    private String versionFileUrl;
    private String versionFileName;

    public ScreenResponse(Screen screen) {
        this.id = screen.getId();
        this.name = screen.getName();
        this.screenType = screen.getScreenType();
        this.location = screen.getLocation();
        this.solutionType = screen.getSolutionType();
//        this.pixelScreen = screen.getPixelScreen();

        this.pixelPitchWidth = screen.getPixelPitchWidth();
        this.pixelPitchHeight = screen.getPixelPitchHeight();
        this.screenWidth = screen.getScreenWidth();
        this.screenHeight = screen.getScreenHeight();

        this.description = screen.getDescription();
        this.powerSupply = screen.getPowerSupply();
        this.powerSupplyQuantity = screen.getPowerSupplyQuantity();
        this.sparePowerSupplyQuantity = screen.getSparePowerSupplyQuantity();
        this.receivingCard = screen.getReceivingCard();
        this.receivingCardQuantity = screen.getReceivingCardQuantity();
        this.spareReceivingCardQuantity = screen.getSpareReceivingCardQuantity();
        //power cable
        this.mainPowerCable = screen.getMainPowerCable();
        this.mainPowerCableQuantity = screen.getMainPowerCableQuantity();
        this.spareMainPowerCableQuantity = screen.getSpareMainPowerCableQuantity();

        this.loopPowerCable = screen.getLoopPowerCable();
        this.loopPowerCableQuantity = screen.getLoopPowerCableQuantity();
        this.spareLoopPowerCableQuantity = screen.getSpareLoopPowerCableQuantity();

        //data cable
        this.mainDataCable = screen.getMainDataCable();
        this.mainDataCableQuantity = screen.getMainDataCableQuantity();
        this.spareMainDataCableQuantity = screen.getSpareMainDataCableQuantity();

        this.loopDataCable = screen.getLoopDataCable();
        this.loopDataCableQuantity = screen.getLoopDataCableQuantity();
        this.spareLoopDataCableQuantity = screen.getSpareLoopDataCableQuantity();

        this.media = screen.getMedia();
        this.mediaQuantity = screen.getMediaQuantity();
        this.spareMediaQuantity = screen.getSpareMediaQuantity();
        this.fan = screen.getFan();
        this.fanQuantity = screen.getFanQuantity();
        this.hub = screen.getHub();
        this.hubQuantity = screen.getHubQuantity();
        this.spareHubQuantity = screen.getSpareHubQuantity();
        this.resolution = screen.getResolution();
        this.createdAt = screen.getCreatedAt();
        this.moduleList = screen.getModuleList();
        this.cabinList = screen.getCabinList();

        // Map file-related fields
        this.connectionFileUrl = screen.getConnectionFileUrl();
        this.connectionFileName = screen.getConnectionFileName();
        this.configFileUrl = screen.getConfigFileUrl();
        this.configFileName = screen.getConfigFileName();
        this.versionFileUrl = screen.getVersionFileUrl();
        this.versionFileName = screen.getVersionFileName();
    }
}