package com.celebritysystems.service.impl;

import com.celebritysystems.dto.ModuleDto;
import com.celebritysystems.entity.Module;
import com.celebritysystems.repository.ModuleRepository;
import com.celebritysystems.service.ModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;
    @Override
    public Optional<Module> createModule(ModuleDto moduleRequest) {
        Module module = Module.builder()
                .pixelHeight(moduleRequest.getPixelHeight())
                .pixelWidth(moduleRequest.getPixelWidth())
                .quantity(moduleRequest.getQuantity())
                .build();

        return Optional.of(moduleRepository.save(module));
    }
}
