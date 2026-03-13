package com.retailos.sync.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.sync.domain.SyncEvent;
import com.retailos.sync.service.SyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sync")
@RequiredArgsConstructor
@Tag(name = "Sync", description = "Offline-first data synchronization")
public class SyncController {

    private final SyncService syncService;

    @PostMapping("/push")
    @Operation(summary = "Push offline changes to server")
    public ResponseEntity<ApiResponse<List<SyncEvent>>> push(@RequestBody List<SyncEvent> events) {
        return ResponseEntity.ok(ApiResponse.success(syncService.pushChanges(events)));
    }

    @GetMapping("/pull/{deviceId}")
    @Operation(summary = "Pull pending changes for device")
    public ResponseEntity<ApiResponse<List<SyncEvent>>> pull(@PathVariable String deviceId) {
        return ResponseEntity.ok(ApiResponse.success(syncService.getPendingForDevice(deviceId)));
    }

    @GetMapping("/history")
    @Operation(summary = "Sync history")
    public ResponseEntity<ApiResponse<Page<SyncEvent>>> history(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(syncService.getSyncHistory(pageable)));
    }
}
