package com.celebritysystems.service.impl;

import com.celebritysystems.dto.CabinDto;
import com.celebritysystems.entity.Cabin;
import com.celebritysystems.repository.CabinRepository;
import com.celebritysystems.service.CabinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CabinServiceImpl implements CabinService {
    private final CabinRepository cabinRepository;

    @Override
    public Optional<Cabin> createCabin(CabinDto cabinRequest) {
        Cabin cabin = Cabin.builder()
                .height(cabinRequest.getHeight())
                .width(cabinRequest.getWidth())
                .quantity(cabinRequest.getQuantity())
                .type(cabinRequest.getType())
                .build();

        return Optional.of(cabinRepository.save(cabin));
    }
}
