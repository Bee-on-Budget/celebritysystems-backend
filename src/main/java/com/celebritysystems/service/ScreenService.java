package com.celebritysystems.service;

import com.celebritysystems.dto.*;
import com.celebritysystems.dto.statistics.AnnualStats;
import com.celebritysystems.dto.statistics.MonthlyStats;
import com.celebritysystems.entity.Screen;

import java.util.List;
import java.util.Optional;

public interface ScreenService {
    Optional<Screen> createScreen(CreateScreenRequestDto createScreenRequestDto, List<CabinDto> cabinDtoList,
            List<ModuleDto> moduleDtoList);

    PaginatedResponse<ScreenResponse> getAllScreens(Integer page);

    long getScreenCountByMonthAndYear(int month, int year);

    List<MonthlyStats> getMonthlyScreenStats();

    List<AnnualStats> getAnnualScreenStats();
    List<ScreenResponse> getScreensWithoutContracts();

    // Optional<ScreenResponse> getScreenById(Long screenId) ;
    // Optional<ScreenResponse> getScreenById(Long id);
    public Optional<ScreenResponse> getScreenById(Long id);

    List<Screen> searchScreenByName(String name);

    void deleteScreen(Long id);

    Long getScreensCount();

    Screen patchScreenResolution(Long screenId, PatchScreenResolutionDTO patchScreenResolutionDTO);
}
