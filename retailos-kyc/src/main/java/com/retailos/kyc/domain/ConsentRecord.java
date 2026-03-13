package com.retailos.kyc.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

/**
 * ConsentRecord - DPDP Act (2023) compliance.
 * Tracks user consent for data processing with purpose & withdrawal support.
 */
@Entity
@Table(name = "consent_records")
@Getter
@Setter
@NoArgsConstructor
public class ConsentRecord extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "purpose", nullable = false)
    private String purpose;

    @Column(name = "consent_text", nullable = false, columnDefinition = "TEXT")
    private String consentText;

    @Column(name = "granted", nullable = false)
    private boolean granted;

    @Column(name = "granted_at")
    private Instant grantedAt;

    @Column(name = "withdrawn_at")
    private Instant withdrawnAt;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    public void grant(String ip, String ua) {
        this.granted = true;
        this.grantedAt = Instant.now();
        this.ipAddress = ip;
        this.userAgent = ua;
    }

    public void withdraw() {
        this.granted = false;
        this.withdrawnAt = Instant.now();
    }
}
