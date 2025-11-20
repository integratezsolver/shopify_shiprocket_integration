package com.integratez.platform.modules.common.domain;


import com.integratez.platform.modules.auth.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;


@Entity
@Table(name = "integration_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationCredentials {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        // Many credentials belong to one integration account
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "integration_account_id", nullable = false)
        private IntegrationAccount integrationAccount;

        @Column(name = "key_cred", nullable = false, length = 150)
        private String key;

        @Column(name = "value", nullable = false, columnDefinition = "TEXT")
        private String value;

        @Column(name = "created_at", nullable = false)
        private Instant createdAt;


        @Column(name = "updated_at", nullable = false)
        private Instant updatedAt;


        @PrePersist
        public void prePersist() {
                Instant now = Instant.now();
                this.createdAt = now;
                this.updatedAt = now;
        }

        @PreUpdate
        public void preUpdate() {
                this.updatedAt = Instant.now();
        }



}
