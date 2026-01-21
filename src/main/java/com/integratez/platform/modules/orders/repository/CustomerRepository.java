package com.integratez.platform.modules.orders.repository;

import com.integratez.platform.modules.orders.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCustomerId(Long customerId);
}
