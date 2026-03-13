package com.retailos.tenant.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.tenant.domain.Store;
import com.retailos.tenant.domain.Tenant;
import com.retailos.tenant.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tenant")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant & store management")
public class TenantController {

    private final TenantService tenantService;

    @GetMapping("/me")
    @Operation(summary = "Get current tenant")
    public ResponseEntity<ApiResponse<Tenant>> getCurrentTenant() {
        return ResponseEntity.ok(ApiResponse.success(tenantService.getCurrentTenant()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    @Operation(summary = "Update tenant")
    public ResponseEntity<ApiResponse<Tenant>> updateTenant(
            @PathVariable UUID id, @RequestBody Tenant updates) {
        return ResponseEntity.ok(ApiResponse.success(tenantService.updateTenant(id, updates)));
    }

    // ==================== Stores ====================

    @GetMapping("/stores")
    @Operation(summary = "List stores")
    public ResponseEntity<ApiResponse<List<Store>>> getStores() {
        return ResponseEntity.ok(ApiResponse.success(tenantService.getStores()));
    }

    @PostMapping("/stores")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    @Operation(summary = "Create store")
    public ResponseEntity<ApiResponse<Store>> createStore(@RequestBody Store store) {
        return ResponseEntity.ok(ApiResponse.success(tenantService.createStore(store)));
    }

    @PutMapping("/stores/{storeId}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    @Operation(summary = "Update store")
    public ResponseEntity<ApiResponse<Store>> updateStore(
            @PathVariable UUID storeId, @RequestBody Store updates) {
        return ResponseEntity.ok(ApiResponse.success(tenantService.updateStore(storeId, updates)));
    }
}
