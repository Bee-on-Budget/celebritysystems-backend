package com.celebritysystems.service.impl;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
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

    private ScreenResponse mapToResponse(Screen screen) {
        ScreenResponse response = new ScreenResponse();
        response.setId(screen.getId());
        response.setName(screen.getName());
        // Map other fields if needed
        return response;
    }

    @Override
//    @Transactional
    public Optional<Screen> createScreen(CreateScreenRequestDto screenDTO, List<CabinDto> cabinDtoList, List<ModuleDto> moduleDtoList) {
        // Create Screen
        Screen screen = mapScreenDtoToEntity(screenDTO);

        if (screenDTO.getSolutionTypeInScreen() == SolutionTypeInScreen.MODULE_SOLUTION) {
            List<Module> moduleList = new ArrayList<>();
            double resolution;
            double heightResolution = 0;
            double widthResolution = 0;

            for (ModuleDto dto : moduleDtoList) {
                Module module = new Module();
                module.setQuantity(dto.getQuantity());
                module.setBatchNumber(dto.getModuleBatchNumber());
                module.setHeightQuantity(dto.getHeightQuantity());
                module.setWidthQuantity(dto.getWidthQuantity());
                module.setHeight(dto.getHeight());
                module.setWidth(dto.getWidth());

                // Calculate and add to resolution
                if(dto.getIsHeight().equals(Boolean.TRUE)){
                    heightResolution += dto.getHeight() * dto.getHeightQuantity();
                }
                if(dto.getIsWidth().equals(Boolean.FALSE)){
                    widthResolution += dto.getWidth() * dto.getWidthQuantity();
                }

                moduleList.add(module);
            }
            screen.setModuleList(moduleRepository.saveAll(moduleList));
            resolution = heightResolution * widthResolution;
            screen.setResolution(resolution);
        }

        /////////////////////////////////////
        if (screenDTO.getSolutionTypeInScreen() == SolutionTypeInScreen.CABINET_SOLUTION) {
            List<Cabin> cabinets = new ArrayList<>();
            double resolution;
            double heightResolution = 0;
            double widthResolution = 0;

            for (CabinDto dto : cabinDtoList) {
                Cabin cabinet = new Cabin();
                cabinet.setCabinName(dto.getCabinetName());
                cabinet.setQuantity(dto.getQuantity());
                cabinet.setHeightQuantity(dto.getHeightQuantity());
                cabinet.setWidthQuantity(dto.getWidthQuantity());
                cabinet.setHeight(dto.getHeight());
                cabinet.setWidth(dto.getWidth());

                //Calculate resolution from Cabinets
                if(dto.getIsHeight().equals(Boolean.TRUE)){
                    heightResolution += dto.getHeight() * dto.getHeightQuantity();
                }
                if(dto.getIsWidth().equals(Boolean.FALSE)){
                    widthResolution += dto.getWidth() * dto.getWidthQuantity();
                }

                if (dto.getModuleDto() == null) {
                    cabinets.add(cabinet);
                    continue;
                }

                Module tempModule = new Module();
                tempModule.setBatchNumber(dto.getModuleDto().getModuleBatchNumber());
                tempModule.setHeightQuantity(dto.getHeightQuantity());
                tempModule.setWidthQuantity(dto.getWidthQuantity());
                tempModule.setWidth(dto.getModuleDto().getWidth());
                tempModule.setHeight(dto.getModuleDto().getHeight());
                tempModule.setQuantity(dto.getModuleDto().getQuantity());

                cabinet.setModule(moduleRepository.save(tempModule));

                cabinets.add(cabinet);
            }
            screen.setCabinList(cabinRepository.saveAll(cabinets));
            resolution = heightResolution * widthResolution;
            screen.setResolution(resolution);
        }
        ////////////////////////////////////
        Screen savedScreen = screenRepository.save(screen);

        return Optional.of(savedScreen);
    }

    @Override
    public PaginatedResponse<ScreenResponse> getAllScreens(Integer page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("id").descending());

        Page<Screen> screenPage = screenRepository.findAll(pageable);
        List<ScreenResponse> content = screenPage.getContent().stream().map(screen -> new ScreenResponse(screen)).toList();

        PaginatedResponse<ScreenResponse> response = new PaginatedResponse<>();
        response.setContent(content);
        response.setPageNumber(screenPage.getNumber());
        response.setPageSize(screenPage.getSize());
        response.setTotalElements(screenPage.getTotalElements());
        response.setTotalPages(screenPage.getTotalPages());
        response.setHasNext(screenPage.hasNext());
        response.setHasPrevious(screenPage.hasPrevious());

        return response;
    }

    private Screen mapScreenDtoToEntity(CreateScreenRequestDto dto) {
        Screen screen = new Screen();
        // Basic Fields
        screen.setName(dto.getName());
        screen.setScreenType(dto.getScreenType());
        screen.setSolutionType(dto.getSolutionTypeInScreen());
        screen.setLocation(dto.getLocation());
        screen.setDescription(dto.getDescription());
        screen.setPixelScreen(dto.getPixelScreen());

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

        // Hub Section
        screen.setHub(dto.getHub());
        screen.setHubQuantity(dto.getHubQuantity());
        screen.setSpareHubQuantity(dto.getSpareHubQuantity());

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

    @Override
    public long getScreenCountByMonthAndYear(int month, int year) {
        return screenRepository.countByMonthAndYear(month, year);
    }

    @Override
    public List<MonthlyStats> getMonthlyScreenStats() {
        return screenRepository.getMonthlyScreenRegistrationStats()
                .stream()
                .map(record -> new MonthlyStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).intValue(),
                        ((Number) record[2]).longValue()))
                .toList();
    }

    @Override
    public List<AnnualStats> getAnnualScreenStats() {
        return screenRepository.getAnnualScreenRegistrationStats()
                .stream()
                .map(record -> new AnnualStats(
                        ((Number) record[0]).intValue(),
                        ((Number) record[1]).longValue()))
                .toList();
    }

    @Override
    public Optional<ScreenResponse> getScreenById(Long id) {
        return screenRepository.findById(id)
                .map(screen -> {
                    ScreenResponse screenResponse = new ScreenResponse();
                    screenResponse.setId(screen.getId());
                    screenResponse.setName(screen.getName());
                    screenResponse.setLocation(screen.getLocation());
                    return screenResponse;
                });
    }

    @Override
    public List<Screen> searchScreenByName(String name) {
        return screenRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    public void deleteScreen(Long id) {
        if (!screenRepository.existsById(id)) {
            // Or throw a custom exception
            throw new RuntimeException("Screen not found with id: " + id);
        }
        screenRepository.deleteById(id);
    }

    @Override
    public Long getScreensCount() {
        return screenRepository.count();
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

