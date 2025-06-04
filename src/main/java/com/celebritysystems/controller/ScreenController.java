package com.celebritysystems.controller;

import com.celebritysystems.dto.*;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.entity.enums.SolutionTypeInScreen;
import com.celebritysystems.service.ScreenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

import java.util.List;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
@CrossOrigin
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createScreen(@ModelAttribute CreateScreenRequestDto request) throws JsonProcessingException {
        try {
            System.out.println("**********************************************************************************");
            System.out.println(request);
            System.out.println("**********************************************************************************");
            //////////////////////////////////////////////////////
            if(request.getSolutionTypeInScreen() == SolutionTypeInScreen.CABINET_SOLUTION){
                ObjectMapper objectMapper = new ObjectMapper();

                // Parse the JSON string manually
                List<CabinDto> cabinDtoList = objectMapper.readValue(
                        request.getCabinDtoListJson(),
                        new TypeReference<List<CabinDto>>() {}
                );

                System.out.println("cabinDtoList IS ----------------------- " + cabinDtoList);
                System.out.println("Module is " + cabinDtoList.get(0).getModuleDto());
                screenService.createScreen(request, cabinDtoList, null);
            }else {
                ObjectMapper objectMapper = new ObjectMapper();

                // Parse the JSON string manually
                List<ModuleDto> moduleDtoList = objectMapper.readValue(
                        request.getModuleDtoListJson(),
                        new TypeReference<List<ModuleDto>>() {}
                );

                System.out.println("ModuleDtoList IS ----------------------- " + moduleDtoList);
                screenService.createScreen(request, null, moduleDtoList);
            }
            /////////////////////////////////////////////////////
            return ResponseEntity.ok("Screen created successfully");
        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating screen: " + e.getMessage());
        }
    }


    @GetMapping()
    ResponseEntity<PaginatedResponse<ScreenResponse>> getAllScreens(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        PaginatedResponse<ScreenResponse> screenPage = screenService.getAllScreens(page);
//        Page<ConsultationRes> responsePage = consultationPage.map(ConsultationRes::new);
        return ResponseEntity.ok().body(screenPage);

    }
}
