package com.integratez.platform.modules.common.repository;

import com.integratez.platform.modules.common.domain.IntegrationAccount;
import com.integratez.platform.modules.common.domain.IntegrationCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IntegrationCredentialsRepository  extends  JpaRepository<IntegrationCredentials, Long> {
    Optional<IntegrationCredentials> findByKeyAndValue(String shopName, String shop);

    @Query("SELECT ic.value FROM IntegrationCredentials ic WHERE ic.key = 'shopName' AND ic.user.id = :userId")
    Optional<String> findShopifyShopNameByUserId(@Param("userId") Long userId);


    @Query("SELECT ic.value FROM IntegrationCredentials ic WHERE ic.key = 'token' AND ic.user.id = :userId")
    String getShopifyAccessToken(@Param("userId") Long userId);

    List<IntegrationCredentials> findByIntegrationAccount(IntegrationAccount integrationAccount);
}
