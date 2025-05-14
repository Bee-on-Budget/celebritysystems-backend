package com.celebritysystems.service;

import com.celebritysystems.dto.CompanyDto;
import com.celebritysystems.dto.ScreenDto;
import com.celebritysystems.entity.Company;
import com.celebritysystems.entity.Screen;

import java.util.Optional;

public interface ScreenService {
    Optional<Screen> createScreen(ScreenDto screenDto);
}
