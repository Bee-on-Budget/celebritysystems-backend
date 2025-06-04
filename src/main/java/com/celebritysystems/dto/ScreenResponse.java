package com.celebritysystems.dto;

import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.ScreenType;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    private String media;

    private Long mediaQuantity;

    private Long spareMediaQuantity;

    private String fan;

    private Long fanQuantity;

    private Double resolution;

    private LocalDateTime createdAt;

    private List<Module> moduleList;

    private List<Cabin> cabinList;

    public ScreenResponse(Screen screen) {
        this.id = screen.getId();
        this.name = screen.getName();
        this.screenType = screen.getScreenType();
        this.location = screen.getLocation();
        this.solutionType = screen.getSolutionType();
        this.powerSupply = screen.getPowerSupply();
        this.powerSupplyQuantity = screen.getPowerSupplyQuantity();
        this.sparePowerSupplyQuantity = screen.getSparePowerSupplyQuantity();
        this.receivingCard = screen.getReceivingCard();
        this.receivingCardQuantity = screen.getReceivingCardQuantity();
        this.spareReceivingCardQuantity = screen.getSpareReceivingCardQuantity();
        this.cable = screen.getCable();
        this.cableQuantity = screen.getCableQuantity();
        this.spareCableQuantity = screen.getSpareCableQuantity();
        this.powerCable = screen.getPowerCable();
        this.powerCableQuantity = screen.getPowerCableQuantity();
        this.sparePowerCableQuantity = screen.getSparePowerCableQuantity();
        this.dataCable = screen.getDataCable();
        this.dataCableQuantity = screen.getDataCableQuantity();
        this.spareDataCableQuantity = screen.getSpareDataCableQuantity();
        this.media = screen.getMedia();
        this.mediaQuantity = screen.getMediaQuantity();
        this.spareMediaQuantity = screen.getSpareMediaQuantity();
        this.fan = screen.getFan();
        this.fanQuantity = screen.getFanQuantity();
        this.resolution = screen.getResolution();
        this.createdAt = screen.getCreatedAt();
        this.moduleList = screen.getModuleList();
        this.cabinList = screen.getCabinList();
    }
}
