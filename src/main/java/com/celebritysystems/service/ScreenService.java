package com.celebritysystems.service;

import com.celebritysystems.dto.CreateScreenRequestDto;
import com.celebritysystems.entity.Screen;

import java.util.Optional;

public interface ScreenService {
    Optional<Screen> createScreen(CreateScreenRequestDto createScreenRequestDto);
}
