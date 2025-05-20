package com.celebritysystems.repository;

import com.celebritysystems.entity.Cabin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CabinRepository extends JpaRepository<Cabin, Long> {
}
