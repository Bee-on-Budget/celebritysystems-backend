package com.celebritysystems.service;

import com.celebritysystems.dto.CreateScreenRequestDto;
import com.celebritysystems.entity.Screen;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ScreenService {
    Optional<Screen> createScreen(CreateScreenRequestDto createScreenRequestDto);
    Page<Screen> getAllScreens(Integer page);
}
