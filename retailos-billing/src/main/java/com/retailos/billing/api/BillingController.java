package com.retailos.billing.api;

import com.retailos.billing.domain.Bill;
import com.retailos.billing.service.BillingService;
import com.retailos.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
@Tag(name = "Billing", description = "POS billing & sales")
public class BillingController {

    private final BillingService billingService;

    @GetMapping
    @Operation(summary = "List bills (paginated)")
    public ResponseEntity<ApiResponse<Page<Bill>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(billingService.listBills(pageable)));
    }

    @GetMapping("/store/{storeId}")
    @Operation(summary = "List bills by store")
    public ResponseEntity<ApiResponse<Page<Bill>>> listByStore(
            @PathVariable UUID storeId, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(billingService.listBillsByStore(storeId, pageable)));
    }

    @GetMapping("/date-range")
    @Operation(summary = "List bills by date range")
    public ResponseEntity<ApiResponse<Page<Bill>>> listByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(billingService.listBillsByDateRange(from, to, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill by ID")
    public ResponseEntity<ApiResponse<Bill>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(billingService.getBillById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER')")
    @Operation(summary = "Create bill")
    public ResponseEntity<ApiResponse<Bill>> create(@RequestBody Bill bill) {
        return ResponseEntity.ok(ApiResponse.success(billingService.createBill(bill)));
    }

    @PostMapping("/{id}/finalize")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','CASHIER')")
    @Operation(summary = "Finalize a draft bill")
    public ResponseEntity<ApiResponse<Bill>> finalize(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(billingService.finalizeBill(id)));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    @Operation(summary = "Cancel a bill")
    public ResponseEntity<ApiResponse<Bill>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(billingService.cancelBill(id)));
    }
}
