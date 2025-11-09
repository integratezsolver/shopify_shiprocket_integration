package com.integratez.platform.modules.auth.repository;


import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.integratez.platform.modules.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findUserByEmail(String email);
}