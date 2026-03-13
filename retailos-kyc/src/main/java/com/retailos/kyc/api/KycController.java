package com.retailos.kyc.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.kyc.domain.ConsentRecord;
import com.retailos.kyc.domain.KycDocument;
import com.retailos.kyc.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC & Consent", description = "KYC document verification & DPDP compliance")
public class KycController {

    private final KycService kycService;

    @PostMapping("/documents")
    @Operation(summary = "Submit KYC document")
    public ResponseEntity<ApiResponse<KycDocument>> submit(@RequestBody KycDocument doc) {
        return ResponseEntity.ok(ApiResponse.success(kycService.submitDocument(doc)));
    }

    @GetMapping("/documents/me")
    @Operation(summary = "Get my KYC documents")
    public ResponseEntity<ApiResponse<List<KycDocument>>> myDocuments() {
        return ResponseEntity.ok(ApiResponse.success(kycService.getMyDocuments()));
    }

    @GetMapping("/documents/pending")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','PLATFORM_ADMIN')")
    @Operation(summary = "List pending KYC documents")
    public ResponseEntity<ApiResponse<Page<KycDocument>>> pending(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(kycService.getPendingDocuments(pageable)));
    }

    @PostMapping("/documents/{id}/approve")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','PLATFORM_ADMIN')")
    @Operation(summary = "Approve KYC document")
    public ResponseEntity<ApiResponse<KycDocument>> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(kycService.approveDocument(id)));
    }

    @PostMapping("/documents/{id}/reject")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','PLATFORM_ADMIN')")
    @Operation(summary = "Reject KYC document")
    public ResponseEntity<ApiResponse<KycDocument>> reject(
            @PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(kycService.rejectDocument(id, reason)));
    }

    // ==================== Consent ====================

    @PostMapping("/consent")
    @Operation(summary = "Grant consent (DPDP)")
    public ResponseEntity<ApiResponse<ConsentRecord>> grantConsent(
            @RequestParam String purpose, @RequestParam String consentText,
            HttpServletRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                kycService.grantConsent(purpose, consentText,
                        request.getRemoteAddr(), request.getHeader("User-Agent"))
        ));
    }

    @DeleteMapping("/consent/{id}")
    @Operation(summary = "Withdraw consent")
    public ResponseEntity<ApiResponse<String>> withdrawConsent(@PathVariable UUID id) {
        kycService.withdrawConsent(id);
        return ResponseEntity.ok(ApiResponse.success("Consent withdrawn"));
    }

    @GetMapping("/consent/me")
    @Operation(summary = "Get my consent records")
    public ResponseEntity<ApiResponse<List<ConsentRecord>>> myConsents() {
        return ResponseEntity.ok(ApiResponse.success(kycService.getMyConsents()));
    }
}
