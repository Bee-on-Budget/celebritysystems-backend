//package com.celebritysystems.mapper;
//
//import com.celebritysystems.dto.CreateScreenRequestDto;
//import com.celebritysystems.entity.Cabin;
//import com.celebritysystems.entity.Module;
//import com.celebritysystems.entity.Screen;
//import com.celebritysystems.entity.repository.CabinRepository;
//import com.celebritysystems.entity.repository.ModuleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//
//import java.io.IOException;
//
//@Component
//@RequiredArgsConstructor
//public class ScreenMapper {
//    private final ModuleRepository moduleRepository;
//    private final CabinRepository cabinRepository;
//
//    public Screen toEntity(CreateScreenRequestDto dto) {
//        // Map basic fields
//        Screen.ScreenBuilder screenBuilder = Screen.builder()
//                .name(dto.getName())
//                .screenType(dto.getScreenType())
//                .location(dto.getLocation())
//                .height(dto.getHeight())
//                .width(dto.getWidth())
//                .powerSupply(dto.getPowerSupply())
//                .powerSupplyQuantity(dto.getPowerSupplyQuantity())
//                .sparePowerSupplyQuantity(dto.getSparePowerSupplyQuantity())
//                .receivingCard(dto.getReceivingCard())
//                .receivingCardQuantity(dto.getReceivingCardQuantity())
//                .spareReceivingCardQuantity(dto.getSpareReceivingCardQuantity())
//                .cable(dto.getCable())
//                .cableQuantity(dto.getCableQuantity())
//                .spareCableQuantity(dto.getSpareCableQuantity())
//                .powerCable(dto.getPowerCable())
//                .powerCableQuantity(dto.getPowerCableQuantity())
//                .sparePowerCableQuantity(dto.getSparePowerCableQuantity())
//                .dataCable(dto.getDataCable())
//                .dataCableQuantity(dto.getDataCableQuantity())
//                .spareDataCableQuantity(dto.getSpareDataCableQuantity());
//
//        // Convert MultipartFile to byte[]
//        try {
//            if (dto.getConnectionFile() != null)
//                screenBuilder.connection(dto.getConnectionFile().getBytes());
//
//            if (dto.getConfigFile() != null)
//                screenBuilder.config(dto.getConfigFile().getBytes());
//
//            if (dto.getVersionFile() != null)
//                screenBuilder.version(dto.getVersionFile().getBytes());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read uploaded files", e);
//        }
//
//        // Convert nested DTOs
//        if (dto.getCabin() != null) {
//            Cabin cabin = Cabin.builder()
//                    .quantity(dto.getCabin().getQuantity())
//                    .height(dto.getCabin().getHeight())
//                    .width(dto.getCabin().getWidth())
//                    .type(dto.getCabin().getType())
//                    .build();
////            Cabin cabinTemp = cabinRepository.save(cabin);
//            screenBuilder.cabin(cabin);
//        }
//
//        if (dto.getModule() != null) {
//            Module module = Module.builder()
//                    .quantity(dto.getModule().getQuantity())
//                    .height(dto.getModule().getHeight())
//                    .width(dto.getModule().getWidth())
//                    .build();
////            Module moduleTemp = moduleRepository.save(module);
//            screenBuilder.module(module);
//        }
//
//        return screenBuilder.build();
//    }
//}