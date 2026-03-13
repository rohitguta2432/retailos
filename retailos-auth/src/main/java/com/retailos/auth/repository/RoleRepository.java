package com.retailos.auth.repository;

import com.retailos.auth.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByNameAndTenantId(String name, UUID tenantId);

    List<Role> findByTenantId(UUID tenantId);

    /** Find system template roles (tenant_id IS NULL) */
    List<Role> findByTenantIdIsNullAndSystemRoleTrue();

    Optional<Role> findByNameAndTenantIdIsNull(String name);
}
