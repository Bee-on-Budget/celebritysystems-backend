package com.celebritysystems.service;

import com.celebritysystems.dto.CabinDto;
import com.celebritysystems.entity.Cabin;

import java.util.Optional;

public interface CabinService {
    Optional<Cabin> createCabin(CabinDto cabinRequest);
}
