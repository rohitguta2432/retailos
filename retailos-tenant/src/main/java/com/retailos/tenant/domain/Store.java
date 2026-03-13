package com.retailos.tenant.domain;

import com.retailos.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Store entity - a physical retail location belonging to a tenant.
 * Multi-store support allows chains and franchises.
 */
@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class Store extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "store_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreType storeType = StoreType.RETAIL;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StoreStatus status = StoreStatus.ACTIVE;

    @Column(name = "address_line1")
    private String addressLine1;

    @Column(name = "address_line2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "phone")
    private String phone;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    public enum StoreType {
        RETAIL, WHOLESALE, WAREHOUSE_ATTACHED, FRANCHISE
    }

    public enum StoreStatus {
        ACTIVE, INACTIVE, CLOSED
    }
}
