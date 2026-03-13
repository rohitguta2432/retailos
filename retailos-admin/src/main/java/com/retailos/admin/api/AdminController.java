package com.retailos.admin.api;

import com.retailos.admin.service.AdminService;
import com.retailos.common.dto.ApiResponse;
import com.retailos.tenant.domain.Tenant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Platform Admin", description = "Platform administration operations")
@PreAuthorize("hasRole('PLATFORM_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/tenants")
    @Operation(summary = "List all tenants")
    public ResponseEntity<ApiResponse<Page<Tenant>>> listTenants(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(adminService.listAllTenants(pageable)));
    }

    @GetMapping("/tenants/{id}")
    @Operation(summary = "Get tenant details")
    public ResponseEntity<ApiResponse<Tenant>> getTenant(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.getTenantById(id)));
    }

    @PostMapping("/tenants")
    @Operation(summary = "Create new tenant")
    public ResponseEntity<ApiResponse<Tenant>> createTenant(@RequestBody Tenant tenant) {
        return ResponseEntity.ok(ApiResponse.success(adminService.createTenantForRetailer(tenant)));
    }

    @PostMapping("/tenants/{id}/activate")
    @Operation(summary = "Activate tenant")
    public ResponseEntity<ApiResponse<Tenant>> activate(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(adminService.activateTenant(id)));
    }

    @PostMapping("/tenants/{id}/suspend")
    @Operation(summary = "Suspend tenant")
    public ResponseEntity<ApiResponse<Tenant>> suspend(
            @PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(adminService.suspendTenant(id, reason)));
    }

    @GetMapping("/stats")
    @Operation(summary = "Platform stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> stats() {
        return ResponseEntity.ok(ApiResponse.success(adminService.getPlatformStats()));
    }
}
