package com.celebritysystems.service.impl;

import com.celebritysystems.dto.ScreenDto;
import com.celebritysystems.entity.Screen;
import com.celebritysystems.service.ScreenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScreenServiceImpl implements ScreenService {

    @Override
    public Optional<Screen> createScreen(ScreenDto screenDto) {
        return Optional.empty();
    }
}
