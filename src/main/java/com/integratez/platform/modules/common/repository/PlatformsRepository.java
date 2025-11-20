package com.integratez.platform.modules.common.repository;

import com.integratez.platform.modules.common.domain.Platforms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PlatformsRepository extends JpaRepository<Platforms, Long> {
    Optional<Platforms> findByName(String platformName);
}
