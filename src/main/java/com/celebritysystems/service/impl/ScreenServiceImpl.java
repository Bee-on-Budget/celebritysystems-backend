package com.celebritysystems.service.impl;

import com.celebritysystems.dto.CreateScreenRequestDto;
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
    public Optional<Screen> createScreen(CreateScreenRequestDto request) {

        Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new IllegalArgumentException("Module not found with ID: " + request.getModuleId()));

        Cabin cabin = cabinRepository.findById(request.getCabinId())
                .orElseThrow(() -> new IllegalArgumentException("Cabin not found with ID: " + request.getCabinId()));

        byte[] connectionBytes = toBytes(request.getConnectionFile());
        byte[] configBytes = toBytes(request.getConfigFile());
        byte[] versionBytes = toBytes(request.getVersionFile());

        Screen screen = Screen.builder()
                .name(request.getName())
                .screenType(request.getScreenType())
                .location(request.getLocation())
                .height(request.getHeight())
                .width(request.getWidth())
                .powerSupply(request.getPowerSupply())
                .powerSupplyQuantity(request.getPowerSupplyQuantity())
                .sparePowerSupplyQuantity(request.getSparePowerSupplyQuantity())
                .receivingCard(request.getReceivingCard())
                .receivingCardQuantity(request.getReceivingCardQuantity())
                .spareReceivingCardQuantity(request.getSpareReceivingCardQuantity())
                .cable(request.getCable())
                .cableQuantity(request.getCableQuantity())
                .spareCableQuantity(request.getSpareCableQuantity())
                .powerCable(request.getPowerCable())
                .powerCableQuantity(request.getPowerCableQuantity())
                .sparePowerCableQuantity(request.getSparePowerCableQuantity())
                .dataCable(request.getDataCable())
                .dataCableQuantity(request.getDataCableQuantity())
                .spareDataCableQuantity(request.getSpareDataCableQuantity())
                .connection(connectionBytes)
                .config(configBytes)
                .version(versionBytes)
                .module(module)
                .cabin(cabin)
                .build();

        return Optional.of(screenRepository.save(screen));
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

