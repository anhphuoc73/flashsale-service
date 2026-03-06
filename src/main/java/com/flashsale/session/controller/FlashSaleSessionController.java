package com.flashsale.session.controller;

import com.flashsale.exception.ApiResponse;
import com.flashsale.product.dto.response.PageResponse;
import com.flashsale.session.dto.request.CreateFlashSaleSessionRequest;
import com.flashsale.session.dto.request.UpdateFlashSaleSessionRequest;
import com.flashsale.session.dto.response.FlashSaleSessionResponse;
import com.flashsale.session.service.FlashSaleSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class FlashSaleSessionController {

    private final FlashSaleSessionService service;

    // CREATE
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<FlashSaleSessionResponse>> create(
            @Valid @RequestBody CreateFlashSaleSessionRequest request) {
        FlashSaleSessionResponse response = service.create(request);

        return ApiResponse.created(
                "Flash sale session created successfully",
                response
        );
    }

    // UPDATE
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FlashSaleSessionResponse>>  update(
            @PathVariable String id,
            @RequestBody UpdateFlashSaleSessionRequest request
    ) {
        FlashSaleSessionResponse response = service.update(id, request);

        return ApiResponse.ok(
                "Flash sale session updated successfully",
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
                "Flash sale session deleted successfully",
                data
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<PageResponse<FlashSaleSessionResponse>>> getAdminSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());

        Page<FlashSaleSessionResponse> sessionPage =
                service.getAdminSessions(pageable);

        PageResponse<FlashSaleSessionResponse> response =
                PageResponse.<FlashSaleSessionResponse>builder()
                        .data(sessionPage.getContent())
                        .currentPage(sessionPage.getNumber())
                        .totalItems(sessionPage.getTotalElements())
                        .totalPages(sessionPage.getTotalPages())
                        .build();

        return ApiResponse.ok("Get session list success", response);
    }


}
