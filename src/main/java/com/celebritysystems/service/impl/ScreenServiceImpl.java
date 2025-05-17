package com.celebritysystems.service.impl;

import com.celebritysystems.dto.CabinDto;
import com.celebritysystems.dto.CreateScreenRequestDto;
import com.celebritysystems.dto.ModuleDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.repository.CabinRepository;
import com.celebritysystems.entity.repository.ModuleRepository;
import com.celebritysystems.entity.repository.ScreenRepository;
import com.celebritysystems.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {
    private final ScreenRepository screenRepository;
    private final ModuleRepository moduleRepository;
    private final CabinRepository cabinRepository;



    @Override
//    @Transactional
    public Optional<Screen> createScreen(CreateScreenRequestDto screenDTO) {

        // Create and save Module
        Module module = new Module();
        module.setHeight(screenDTO.getModuleHeight());
        module.setWidth(screenDTO.getModuleWidth());
        module.setQuantity(screenDTO.getModuleQuantity());
//        Module module = mapModuleDtoToEntity(screenDTO.getModule());
        Module savedModule = moduleRepository.save(module);

        // Create and save Cabin
        Cabin cabin = new Cabin();
        cabin.setHeight(screenDTO.getCabinHeight());
        cabin.setWidth(screenDTO.getCabinWidth());
        cabin.setQuantity(screenDTO.getCabinQuantity());
        cabin.setType(screenDTO.getCabinType());
//        Cabin cabin = mapCabinDtoToEntity(screenDTO.getCabin());
        Cabin savedCabin = cabinRepository.save(cabin);

        // Create Screen
        Screen screen = mapScreenDtoToEntity(screenDTO);
        screen.setModule(savedModule);
        screen.setCabin(savedCabin);

        return Optional.of(screenRepository.save(screen));
    }

    private Module mapModuleDtoToEntity(ModuleDto dto) {
        Module module = new Module();
        module.setHeight(dto.getHeight());
        module.setWidth(dto.getWidth());
        module.setQuantity(dto.getQuantity());
        // Map other fields from ModuleDTO to Module
        return module;
    }

    private Cabin mapCabinDtoToEntity(CabinDto dto) {
        Cabin cabin = new Cabin();
        cabin.setHeight(dto.getHeight());
        cabin.setWidth(dto.getWidth());
        cabin.setQuantity(dto.getQuantity());
        cabin.setType(dto.getType());
        // Map other fields from CabinDTO to Cabin
        return cabin;
    }

    private Screen mapScreenDtoToEntity(CreateScreenRequestDto dto) {
        Screen screen = new Screen();
        // Basic Fields
        screen.setName(dto.getName());
        screen.setScreenType(dto.getScreenType());
        screen.setLocation(dto.getLocation());
        screen.setHeight(dto.getHeight());
        screen.setWidth(dto.getWidth());

        // Power Supply Section
        screen.setPowerSupply(dto.getPowerSupply());
        screen.setPowerSupplyQuantity(dto.getPowerSupplyQuantity());
        screen.setSparePowerSupplyQuantity(dto.getSparePowerSupplyQuantity());

        // Receiving Card Section
        screen.setReceivingCard(dto.getReceivingCard());
        screen.setReceivingCardQuantity(dto.getReceivingCardQuantity());
        screen.setSpareReceivingCardQuantity(dto.getSpareReceivingCardQuantity());

        // Cable Section
        screen.setCable(dto.getCable());
        screen.setCableQuantity(dto.getCableQuantity());
        screen.setSpareCableQuantity(dto.getSpareCableQuantity());

        // Power Cable Section
        screen.setPowerCable(dto.getPowerCable());
        screen.setPowerCableQuantity(dto.getPowerCableQuantity());
        screen.setSparePowerCableQuantity(dto.getSparePowerCableQuantity());

        // Data Cable Section
        screen.setDataCable(dto.getDataCable());
        screen.setDataCableQuantity(dto.getDataCableQuantity());
        screen.setSpareDataCableQuantity(dto.getSpareDataCableQuantity());

        // Binary Data Fields
        screen.setConnection(toBytes(dto.getConnectionFile()));
        screen.setConfig(toBytes( dto.getConfigFile()));
        screen.setVersion( toBytes(dto.getVersionFile()));

        // Note: Module and Cabin are handled separately in the service
        // They will be set after this mapping

        return screen;
    }

    private byte[] toBytes(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file: " + file.getOriginalFilename(), e);
        }
    }
    }

