package com.celebritysystems.repository;

import com.celebritysystems.entity.Contract;
import com.celebritysystems.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
