package com.flashsale.purchase.controller;

import com.flashsale.exception.ApiResponse;
import com.flashsale.purchase.dto.request.PurchaseRequest;
import com.flashsale.purchase.dto.response.PurchaseResponse;
import com.flashsale.purchase.service.PurchaseService;


import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService service;

    @PostMapping
    public ResponseEntity<ApiResponse<PurchaseResponse>> purchase(
            @RequestAttribute("userId") String userId,
            @RequestBody PurchaseRequest request
    ) {

        PurchaseResponse response = service.purchase(userId, request);

        return ApiResponse.ok(
                "Purchase successful",
                response
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseResponse>>> getPurchases(
            @RequestAttribute("userId") String userId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        String role = authentication.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        Page<PurchaseResponse> response = service.getPurchases(userId, role, page, size);

        return ApiResponse.ok(
                "Get purchases successfully",
                response
        );
    }
}