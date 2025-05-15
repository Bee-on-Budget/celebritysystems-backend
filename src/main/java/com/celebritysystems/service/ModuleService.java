package com.celebritysystems.service;

import com.celebritysystems.dto.ModuleDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.entity.Module;

import java.util.Optional;

public interface ModuleService {
    Optional<Module> createModule(ModuleDto moduleRequest);
}
