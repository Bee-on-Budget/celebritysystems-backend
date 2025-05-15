package com.celebritysystems.service.impl;

import com.celebritysystems.dto.ModuleDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.Module;
import com.celebritysystems.entity.repository.ModuleRepository;
import com.celebritysystems.service.ModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleRepository moduleRepository;
    @Override
    public Optional<Module> createModule(ModuleDto moduleRequest) {
        Module module = Module.builder()
                .height(moduleRequest.getHeight())
                .width(moduleRequest.getWidth())
                .quantity(moduleRequest.getQuantity())
                .build();

        return Optional.of(moduleRepository.save(module));
    }
}
