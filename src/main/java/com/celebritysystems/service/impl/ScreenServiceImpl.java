package com.celebritysystems.service.impl;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import com.celebritysystems.repository.CabinRepository;
import com.celebritysystems.repository.ContractRepository;
import com.celebritysystems.repository.ModuleRepository;
import com.celebritysystems.repository.ScreenRepository;
import com.celebritysystems.service.ScreenService;
import com.celebritysystems.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScreenServiceImpl implements ScreenService {
    private final ScreenRepository screenRepository;
    private final ModuleRepository moduleRepository;
    private final CabinRepository cabinRepository;
    private final ContractRepository contractRepository;
    private final S3Service s3Service;

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
            log.info("I am in : {  if (screenDTO.getSolutionTypeInScreen() == SolutionTypeInScreen.MODULE_SOLUTION) }");

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
                    log.info("I Have a TRUE State }");

                    heightResolution += dto.getHeight() * dto.getHeightQuantity();
                }
                if(dto.getIsWidth().equals(Boolean.TRUE)){
                    log.info("I Have a False State }");

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
                
                cabinet.setIsHeight(dto.getIsHeight());
                cabinet.setIsWidth(dto.getIsWidth());

                //Calculate resolution from Cabinets
                if(dto.getIsHeight().equals(Boolean.TRUE)){
                    heightResolution += dto.getHeight() * dto.getHeightQuantity();
                }
                if(dto.getIsWidth().equals(Boolean.TRUE)){
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
        List<ScreenResponse> content = screenPage.getContent().stream().map(ScreenResponse::new).toList();

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
        screen.setBatchScreen(dto.getBatchScreen());

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

        // File Upload Fields
        if (dto.getConnectionFile() != null && !dto.getConnectionFile().isEmpty()) {
            String connectionUrl = s3Service.uploadFile(dto.getConnectionFile(), "screen-files/connection");
            screen.setConnectionFileUrl(connectionUrl);
            screen.setConnectionFileName(dto.getConnectionFile().getOriginalFilename());
        }
        
        if (dto.getConfigFile() != null && !dto.getConfigFile().isEmpty()) {
            String configUrl = s3Service.uploadFile(dto.getConfigFile(), "screen-files/config");
            screen.setConfigFileUrl(configUrl);
            screen.setConfigFileName(dto.getConfigFile().getOriginalFilename());
        }
        
        if (dto.getVersionFile() != null && !dto.getVersionFile().isEmpty()) {
            String versionUrl = s3Service.uploadFile(dto.getVersionFile(), "screen-files/version");
            screen.setVersionFileUrl(versionUrl);
            screen.setVersionFileName(dto.getVersionFile().getOriginalFilename());
        }

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
                    screenResponse.setScreenType(screen.getScreenType());
                    screenResponse.setSolutionType(screen.getSolutionType());
                    screenResponse.setPixelScreen(screen.getPixelScreen());
                    screenResponse.setDescription(screen.getDescription());
                    screenResponse.setPowerSupply(screen.getPowerSupply());
                    screenResponse.setPowerSupplyQuantity(screen.getPowerSupplyQuantity());
                    screenResponse.setSparePowerSupplyQuantity(screen.getSparePowerSupplyQuantity());
                    screenResponse.setReceivingCard(screen.getReceivingCard());
                    screenResponse.setReceivingCardQuantity(screen.getReceivingCardQuantity());
                    screenResponse.setSpareReceivingCardQuantity(screen.getSpareReceivingCardQuantity());
                    screenResponse.setCable(screen.getCable());
                    screenResponse.setCableQuantity(screen.getCableQuantity());
                    screenResponse.setSpareCableQuantity(screen.getSpareCableQuantity());
                    screenResponse.setPowerCable(screen.getPowerCable());
                    screenResponse.setPowerCableQuantity(screen.getPowerCableQuantity());
                    screenResponse.setSparePowerCableQuantity(screen.getSparePowerCableQuantity());
                    screenResponse.setDataCable(screen.getDataCable());
                    screenResponse.setDataCableQuantity(screen.getDataCableQuantity());
                    screenResponse.setSpareDataCableQuantity(screen.getSpareDataCableQuantity());
                    screenResponse.setMedia(screen.getMedia());
                    screenResponse.setMediaQuantity(screen.getMediaQuantity());
                    screenResponse.setSpareMediaQuantity(screen.getSpareMediaQuantity());
                    screenResponse.setFan(screen.getFan());
                    screenResponse.setFanQuantity(screen.getFanQuantity());
                    screenResponse.setHub(screen.getHub());
                    screenResponse.setHubQuantity(screen.getHubQuantity());
                    screenResponse.setSpareHubQuantity(screen.getSpareHubQuantity());
                    screenResponse.setResolution(screen.getResolution());
                    screenResponse.setCreatedAt(screen.getCreatedAt());
                    screenResponse.setModuleList(screen.getModuleList());
                    screenResponse.setCabinList(screen.getCabinList());
                    
                    // Add file URL and filename fields
                    screenResponse.setConnectionFileUrl(screen.getConnectionFileUrl());
                    screenResponse.setConnectionFileName(screen.getConnectionFileName());
                    screenResponse.setConfigFileUrl(screen.getConfigFileUrl());
                    screenResponse.setConfigFileName(screen.getConfigFileName());
                    screenResponse.setVersionFileUrl(screen.getVersionFileUrl());
                    screenResponse.setVersionFileName(screen.getVersionFileName());
                    
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

    @Override
    public List<ScreenResponse> getScreensWithoutContracts() {
        // Get all screens
        List<Screen> allScreens = screenRepository.findAll();
        
        // Get all screens that are in active contracts
        List<Long> screensInContracts = contractRepository.findActiveContractScreenIds();
        
        // Filter screens that are not in active contracts
        return allScreens.stream()
                .filter(screen -> !screensInContracts.contains(screen.getId()))
                .map(screen -> {
                    ScreenResponse response = new ScreenResponse();
                    response.setId(screen.getId());
                    response.setName(screen.getName());
                    response.setLocation(screen.getLocation());
                    // Set other fields as needed
                    return response;
                })
                .collect(Collectors.toList());
    }
}