package com.flashsale.flashsale.controller;

import com.flashsale.exception.ApiResponse;
import com.flashsale.flashsale.dto.request.CreateFlashSaleItemRequest;
import com.flashsale.flashsale.dto.request.UpdateFlashSaleItemRequest;
import com.flashsale.flashsale.dto.response.FlashSaleItemResponse;
import com.flashsale.flashsale.service.FlashSaleItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/flash-sale-items")
@RequiredArgsConstructor
public class AdminFlashSaleItemController {

    private final FlashSaleItemService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<FlashSaleItemResponse>> create(
            @Valid @RequestBody CreateFlashSaleItemRequest request) {

        FlashSaleItemResponse response = service.create(request);

        return ApiResponse.created(
                "Flash sale item created successfully",
                response
        );
    }

    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlashSaleItemResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateFlashSaleItemRequest request
    ) {

        FlashSaleItemResponse response = service.update(id, request);

        return ApiResponse.ok(
                "Flash sale item updated successfully",
                response
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable String id
    ) {

        service.delete(id);

        Map<String, String> data = new HashMap<>();
        data.put("id", id);

        return ApiResponse.ok(
                "Flash sale item deleted successfully",
                data
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FlashSaleItemResponse>>> getAdminItems(
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String productId,
            Pageable pageable
    ) {

        Page<FlashSaleItemResponse> page =
                service.getAdminItems(sessionId, productId, pageable);

        return ApiResponse.ok(
                "Flash sale items retrieved successfully",
                page
        );
    }
}