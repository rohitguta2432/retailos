package com.retailos.invoice.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.invoice.domain.Invoice;
import com.retailos.invoice.service.InvoiceService;
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
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoices", description = "GST invoice management")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "List invoices")
    public ResponseEntity<ApiResponse<Page<Invoice>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.listInvoices(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID")
    public ResponseEntity<ApiResponse<Invoice>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.getById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ACCOUNTANT')")
    @Operation(summary = "Create invoice")
    public ResponseEntity<ApiResponse<Invoice>> create(
            @RequestBody Invoice invoice,
            @RequestParam(defaultValue = "false") boolean isInterState) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.createInvoice(invoice, isInterState)));
    }

    @PostMapping("/{id}/issue")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ACCOUNTANT')")
    @Operation(summary = "Issue draft invoice")
    public ResponseEntity<ApiResponse<Invoice>> issue(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.issueInvoice(id)));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    @Operation(summary = "Cancel invoice")
    public ResponseEntity<ApiResponse<Invoice>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(invoiceService.cancelInvoice(id)));
    }
}
