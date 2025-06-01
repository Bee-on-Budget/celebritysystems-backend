package com.celebritysystems.service.impl;

import com.celebritysystems.dto.CabinDto;
import com.celebritysystems.dto.CreateScreenRequestDto;
import com.celebritysystems.dto.ModuleDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import com.celebritysystems.repository.CabinRepository;
import com.celebritysystems.repository.ModuleRepository;
import com.celebritysystems.repository.ScreenRepository;
import com.celebritysystems.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {
    private final ScreenRepository screenRepository;
    private final ModuleRepository moduleRepository;
    private final CabinRepository cabinRepository;


    @Override
//    @Transactional
    public Optional<Screen> createScreen(CreateScreenRequestDto screenDTO, List<CabinDto> cabinDtoList, List<ModuleDto> moduleDtoList) {
        // Create Screen
        Screen screen = mapScreenDtoToEntity(screenDTO);

        if (screenDTO.getSolutionTypeInScreen() == SolutionTypeInScreen.MODULE_SOLUTION) {
            List<Module> moduleList = new ArrayList<>();

            for (ModuleDto dto : moduleDtoList) {
                Module module = new Module();
                module.setQuantity(dto.getQuantity());
                module.setBatchNumber(dto.getModuleBatchNumber());
                module.setHeight(dto.getHeight());
                module.setWidth(dto.getWidth());

                moduleList.add(module);
            }
            screen.setModuleList(moduleRepository.saveAll(moduleList));
        }

        /////////////////////////////////////
        if (screenDTO.getSolutionTypeInScreen() == SolutionTypeInScreen.CABINET_SOLUTION) {
            List<Cabin> cabinets = new ArrayList<>();

            for (CabinDto dto : cabinDtoList) {
                Cabin cabinet = new Cabin();
                cabinet.setCabinName(dto.getCabinetName());
                cabinet.setQuantity(dto.getQuantity());
                cabinet.setHeight(dto.getHeight());
                cabinet.setWidth(dto.getWidth());
                if (dto.getModuleDto() == null) {
                    cabinets.add(cabinet);
                    continue;
                }

                Module tempModule = new Module();
                tempModule.setBatchNumber(dto.getModuleDto().getModuleBatchNumber());
                tempModule.setWidth(dto.getModuleDto().getWidth());
                tempModule.setHeight(dto.getModuleDto().getHeight());
                tempModule.setQuantity(dto.getModuleDto().getQuantity());

                cabinet.setModule(moduleRepository.save(tempModule));

                cabinets.add(cabinet);
            }
            screen.setCabinList(cabinRepository.saveAll(cabinets));
        }
        ////////////////////////////////////
        Screen savedScreen = screenRepository.save(screen);

        //TODO
//        Double resolution = screenDTO.getWidth() * screenDTO.getHeight();
//        screen.setResolution(resolution);
        return Optional.of(savedScreen);
    }

    @Override
    public Page<Screen> getAllScreens(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());
        return screenRepository.findAll(pageable);
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
        // Map other fields from CabinDTO to Cabin
        return cabin;
    }

    private Screen mapScreenDtoToEntity(CreateScreenRequestDto dto) {
        Screen screen = new Screen();
        // Basic Fields
        screen.setName(dto.getName());
        screen.setScreenType(dto.getScreenType());
        screen.setSolutionType(dto.getSolutionTypeInScreen());
        screen.setLocation(dto.getLocation());

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

        // Fan section
        screen.setFan(dto.getFan());
        screen.setFanQuantity(dto.getFanQuantity());

        // Binary Data Fields
        screen.setConnection(toBytes(dto.getConnectionFile()));
        screen.setConfig(toBytes(dto.getConfigFile()));
        screen.setVersion(toBytes(dto.getVersionFile()));

        // Media Fields
        screen.setMedia(dto.getMedia());
        screen.setMediaQuantity(dto.getMediaQuantity());
        screen.setSpareMediaQuantity(dto.getSpareMediaQuantity());

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

