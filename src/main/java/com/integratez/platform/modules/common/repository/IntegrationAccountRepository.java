package com.integratez.platform.modules.common.repository;

import com.integratez.platform.modules.common.domain.AccountStatus;
import com.integratez.platform.modules.common.domain.IntegrationAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface IntegrationAccountRepository extends JpaRepository<IntegrationAccount, Long> {
    Optional<IntegrationAccount> findByAccountName(String shop);

    AccountStatus getStatusById(Long id);
}
