package com.celebritysystems.service;

import com.celebritysystems.entity.Screen;
import java.util.List;
import java.util.Optional;

public interface ScreenService {
    List<Screen> findAll();
    Optional<Screen> findById(Long id);
    Screen save(Screen screen);
    void deleteById(Long id);
} 