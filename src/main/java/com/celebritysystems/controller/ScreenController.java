package com.celebritysystems.controller;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import com.celebritysystems.service.ScreenService;
import com.celebritysystems.service.S3Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class ScreenController {

    private final ScreenService screenService;
    private final S3Service s3Service;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createScreen(@ModelAttribute CreateScreenRequestDto request) {
        try {
            log.info("Received createScreen request: {}", request);
            log.info("Solution Type in request: {}", request.getSolutionTypeInScreen());

            if (request.getSolutionTypeInScreen() == SolutionTypeInScreen.CABINET_SOLUTION) {
                log.info("You are in if so solution Type is: {}", request.getSolutionTypeInScreen());

                List<CabinDto> cabinDtoList = parseCabinList(request.getCabinDtoListJson());
                log.debug("Parsed cabinDtoList: {}", cabinDtoList);
                screenService.createScreen(request, cabinDtoList, null);
            } else {
                log.info("You are is else so solution Type is: {}", request.getSolutionTypeInScreen());
                List<ModuleDto> moduleDtoList = parseModuleList(request.getModuleDtoListJson());
                log.debug("Parsed moduleDtoList: {}", moduleDtoList);
                screenService.createScreen(request, null, moduleDtoList);
            }

            return ResponseEntity.ok("Screen created successfully");

        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (JsonProcessingException e) {
            log.error("Error creating screen", e);
            return ResponseEntity.status(500).body("Error creating screen: " + e.getMessage());
        }
    }
@GetMapping("/without-contracts")
public ResponseEntity<List<ScreenResponse>> getScreensWithoutContracts() {
    log.info("Fetching screens without active contracts");
    List<ScreenResponse> screens = screenService.getScreensWithoutContracts();
    return ResponseEntity.ok(screens);
}
    private List<CabinDto> parseCabinList(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, new TypeReference<>() {});
    }

    private List<ModuleDto> parseModuleList(String json) throws JsonProcessingException {
        return new ObjectMapper().readValue(json, new TypeReference<>() {});
    }


    @GetMapping()
    ResponseEntity<PaginatedResponse<ScreenResponse>> getAllScreens(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        PaginatedResponse<ScreenResponse> screenPage = screenService.getAllScreens(page);
//        Page<ConsultationRes> responsePage = consultationPage.map(ConsultationRes::new);
        return ResponseEntity.ok().body(screenPage);

    }

    @GetMapping("/statistic/monthly")
    public List<MonthlyStats> getMonthlyScreenStats() {
        log.info("Fetching monthly screen statistics");
        return screenService.getMonthlyScreenStats();
    }

    @GetMapping("/statistic/annual")
    public List<AnnualStats> getAnnualScreenStats() {
        log.info("Fetching annual screen statistics");
        return screenService.getAnnualScreenStats();
    }

    @GetMapping("/statistic/count")
    public long getCountByMonthAndYear(@RequestParam int month, @RequestParam int year) {
        log.info("Getting count of screens for month {} and year {}", month, year);
        return screenService.getScreenCountByMonthAndYear(month, year);
    }

    @GetMapping("/screen_id/{id}")
    public ResponseEntity<ScreenResponse> getScreenById(@PathVariable Long id) {
        log.info("Fetching screen by ID: {}", id);
        return screenService.getScreenById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Screen with ID {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/search")
    public ResponseEntity<List<Screen>> searchScreensByName(@RequestParam("name") String name) {
        log.info("Searching for screens with name containing: {}", name);
        List<Screen> screens =  screenService.searchScreenByName(name);
        log.info("Found {} screens matching: {}", screens.size(), name);
        return ResponseEntity.ok(screens);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteScreen(@PathVariable Long id) {
        log.info("Deleting screen with id: {}", id);
        try {
            screenService.deleteScreen(id);
            return ResponseEntity.ok("Screen deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting screen with id: {}", id, e);
            return ResponseEntity.status(500).body("Error deleting screen: " + e.getMessage());
        }
    }

    @GetMapping("statistic/total")
    public ResponseEntity<Long> getScreensCount() {
        Long count = screenService.getScreensCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/{id}/download/{fileType}")
    public ResponseEntity<Resource> downloadScreenFile(@PathVariable Long id, 
                                                       @PathVariable String fileType) {
        log.info("Downloading {} file for screen with ID: {}", fileType, id);
        
        ScreenResponse screen = screenService.getScreenById(id)
                .orElse(null);
        if (screen == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String fileUrl = null;
            String fileName = null;
            
            switch (fileType.toLowerCase()) {
                case "connection" -> {
                    // Need to add these fields to ScreenResponse or create a method to get them
                    log.warn("Connection file download not implemented - need to add fileUrl fields to ScreenResponse");
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
                case "config" -> {
                    log.warn("Config file download not implemented - need to add fileUrl fields to ScreenResponse");
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
                case "version" -> {
                    log.warn("Version file download not implemented - need to add fileUrl fields to ScreenResponse");
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to download {} file for screen with ID: {}", fileType, id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/presigned-url/{fileType}")
    public ResponseEntity<String> getScreenFilePresignedUrl(@PathVariable Long id, 
                                                            @PathVariable String fileType,
                                                            @RequestParam(defaultValue = "60") int expirationMinutes) {
        log.info("Generating presigned URL for {} file of screen with ID: {}", fileType, id);
        
        ScreenResponse screen = screenService.getScreenById(id)
                .orElse(null);
        if (screen == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            String fileUrl = null;
            
            switch (fileType.toLowerCase()) {
                case "connection", "config", "version" -> {
                    log.warn("Presigned URL generation not implemented - need to add fileUrl fields to ScreenResponse");
                    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
                }
                default -> {
                    return ResponseEntity.badRequest().build();
                }
            }
            
        } catch (Exception e) {
            log.error("Failed to generate presigned URL for {} file of screen with ID: {}", fileType, id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
