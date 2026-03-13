package com.retailos.auth.repository;

import com.retailos.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u WHERE u.phone = :phone AND u.tenantId = :tenantId")
    Optional<User> findByPhoneAndTenantId(String phone, UUID tenantId);

    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findByPhone(String phone);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.tenantId = :tenantId")
    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);

    boolean existsByPhoneAndTenantId(String phone, UUID tenantId);
}
