package com.celebritysystems.controller;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import com.celebritysystems.service.ScreenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createScreen(@ModelAttribute CreateScreenRequestDto request) {
        try {
            log.info("Received createScreen request: {}", request);

            if (request.getSolutionTypeInScreen() == SolutionTypeInScreen.CABINET_SOLUTION) {
                List<CabinDto> cabinDtoList = parseCabinList(request.getCabinDtoListJson());
                log.debug("Parsed cabinDtoList: {}", cabinDtoList);
                screenService.createScreen(request, cabinDtoList, null);
            } else {
                List<ModuleDto> moduleDtoList = parseModuleList(request.getModuleDtoListJson());
                log.debug("Parsed moduleDtoList: {}", moduleDtoList);
                screenService.createScreen(request, null, moduleDtoList);
            }

            return ResponseEntity.ok("Screen created successfully");

        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (Exception e) {
            log.error("Error creating screen", e);
            return ResponseEntity.status(500).body("Error creating screen: " + e.getMessage());
        }
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

    @GetMapping("statistic/count")
    public ResponseEntity<Long> getScreensCount() {
        Long count = screenService.getScreensCount();
        return ResponseEntity.ok(count);
    }
}
