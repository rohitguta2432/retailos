package com.retailos.inventory.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.inventory.domain.Product;
import com.retailos.inventory.service.ProductService;
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
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Product catalog management")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "List products (paginated)")
    public ResponseEntity<ApiResponse<Page<Product>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.listProducts(pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name, SKU, or barcode")
    public ResponseEntity<ApiResponse<Page<Product>>> search(
            @RequestParam String q, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchProducts(q, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponse<Product>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Get product by barcode (POS scanner)")
    public ResponseEntity<ApiResponse<Product>> getByBarcode(@PathVariable String barcode) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByBarcode(barcode)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','WAREHOUSE_STAFF')")
    @Operation(summary = "Create product")
    public ResponseEntity<ApiResponse<Product>> create(@RequestBody Product product) {
        return ResponseEntity.ok(ApiResponse.success(productService.createProduct(product)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','WAREHOUSE_STAFF')")
    @Operation(summary = "Update product")
    public ResponseEntity<ApiResponse<Product>> update(
            @PathVariable UUID id, @RequestBody Product updates) {
        return ResponseEntity.ok(ApiResponse.success(productService.updateProduct(id, updates)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER')")
    @Operation(summary = "Deactivate product (soft delete)")
    public ResponseEntity<ApiResponse<String>> deactivate(@PathVariable UUID id) {
        productService.deactivateProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product deactivated"));
    }
}
