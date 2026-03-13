package com.retailos.notification.api;

import com.retailos.common.dto.ApiResponse;
import com.retailos.notification.domain.Notification;
import com.retailos.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "In-app notification center")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get my notifications")
    public ResponseEntity<ApiResponse<Page<Notification>>> list(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getMyNotifications(pageable)));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count")
    public ResponseEntity<ApiResponse<Long>> unreadCount() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getUnreadCount()));
    }

    @PostMapping("/mark-all-read")
    @Operation(summary = "Mark all as read")
    public ResponseEntity<ApiResponse<String>> markAllRead() {
        int count = notificationService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success(count + " notifications marked as read"));
    }
}
