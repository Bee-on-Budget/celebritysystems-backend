package com.celebritysystems.service.impl;

import com.celebritysystems.entity.Screen;
import com.celebritysystems.repository.ScreenRepository;
import com.celebritysystems.service.ScreenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepository screenRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Screen> findAll() {
        return screenRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Screen> findById(Long id) {
        return screenRepository.findById(id);
    }

    @Override
    public Screen save(Screen screen) {
        return screenRepository.save(screen);
    }

    @Override
    public void deleteById(Long id) {
        screenRepository.deleteById(id);
    }
} 