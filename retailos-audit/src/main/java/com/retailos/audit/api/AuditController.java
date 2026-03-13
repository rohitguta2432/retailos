package com.retailos.audit.api;

import com.retailos.audit.domain.AuditLog;
import com.retailos.audit.service.AuditService;
import com.retailos.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit trail")
@PreAuthorize("hasAnyRole('OWNER','MANAGER','PLATFORM_ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "List audit logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(auditService.listAuditLogs(pageable)));
    }

    @GetMapping("/entity/{type}/{entityId}")
    @Operation(summary = "Get entity audit trail")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> entityAuditTrail(
            @PathVariable String type, @PathVariable UUID entityId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(
                auditService.getEntityAuditTrail(type, entityId, pageable)));
    }
}
