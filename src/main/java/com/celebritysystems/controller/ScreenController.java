package com.celebritysystems.controller;

import com.celebritysystems.dto.CreateScreenRequestDto;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

@RestController
@RequestMapping("/api/screens")
@RequiredArgsConstructor
@CrossOrigin
public class ScreenController {

    private final ScreenService screenService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createScreen(@ModelAttribute CreateScreenRequestDto request) {
        try {
            screenService.createScreen(request);
            return ResponseEntity.ok("Screen created successfully");
        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body("Invalid file upload");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating screen: " + e.getMessage());
        }
    }

//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> createScreen(
//            @Valid @RequestBody CreateScreenRequestDto screenDTO) {
//        try{
//            Optional<Screen> createdScreen = screenServiceImpl.createScreen(screenDTO);
//            return ResponseEntity.status(HttpStatus.CREATED).body(createdScreen);
//        }
//        catch (MultipartException e) {
//            return ResponseEntity.badRequest().body("Invalid file upload");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Error creating screen: " + e.getMessage());
//        }
//
//    }

    @GetMapping()
    ResponseEntity<Page<Screen>> getAllScreens(@RequestParam(name = "page", defaultValue = "0") Integer page) {
        Page<Screen> screenPage = screenService.getAllScreens(page);
//        Page<ConsultationRes> responsePage = consultationPage.map(ConsultationRes::new);
        return ResponseEntity.ok().body(screenPage);

    }
}
