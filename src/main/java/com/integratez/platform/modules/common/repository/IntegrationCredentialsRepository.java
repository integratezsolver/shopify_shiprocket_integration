package com.integratez.platform.modules.common.repository;

import com.integratez.platform.modules.common.domain.IntegrationCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IntegrationCredentialsRepository  extends  JpaRepository<IntegrationCredentials, Long> {
    Optional<IntegrationCredentials> findByKeyAndValue(String shopName, String shop);
}
