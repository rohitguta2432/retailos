package com.retailos.khata.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.khata.domain.KhataAccount;
import com.retailos.khata.service.KhataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/khata")
@RequiredArgsConstructor
@Tag(name = "Khata", description = "Credit/debit ledger book")
public class KhataController {

    private final KhataService khataService;

    @GetMapping
    @Operation(summary = "List khata accounts (paginated)")
    public ResponseEntity<ApiResponse<Page<KhataAccount>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(khataService.listAccounts(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search khata accounts by name or phone")
    public ResponseEntity<ApiResponse<Page<KhataAccount>>> search(
            @RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(khataService.searchAccounts(q, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get khata account by ID")
    public ResponseEntity<ApiResponse<KhataAccount>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(khataService.getById(id)));
    }

    @GetMapping("/outstanding")
    @Operation(summary = "Get total outstanding balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getOutstanding() {
        return ResponseEntity.ok(ApiResponse.success(khataService.getTotalOutstanding()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ACCOUNTANT')")
    @Operation(summary = "Create khata account")
    public ResponseEntity<ApiResponse<KhataAccount>> create(@RequestBody KhataAccount account) {
        return ResponseEntity.ok(ApiResponse.success(khataService.createAccount(account)));
    }

    @PostMapping("/{id}/credit")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER','ACCOUNTANT')")
    @Operation(summary = "Add credit to khata account")
    public ResponseEntity<ApiResponse<KhataAccount>> addCredit(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success(khataService.addCredit(id, amount, notes)));
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER','ACCOUNTANT')")
    @Operation(summary = "Record payment on khata account")
    public ResponseEntity<ApiResponse<KhataAccount>> recordPayment(
            @PathVariable UUID id,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String notes) {
        return ResponseEntity.ok(ApiResponse.success(khataService.recordPayment(id, amount, notes)));
    }
}
