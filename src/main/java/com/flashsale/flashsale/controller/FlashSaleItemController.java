package com.flashsale.flashsale.controller;

import com.flashsale.exception.ApiResponse;
import com.flashsale.flashsale.dto.response.FlashSaleItemUserResponse;
import com.flashsale.flashsale.service.FlashSaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flash-sale-items")
@RequiredArgsConstructor
public class FlashSaleItemController {

    private final FlashSaleItemService service;
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<List<FlashSaleItemUserResponse>>> getCurrentFlashSaleItems() {
        List<FlashSaleItemUserResponse> items = service.getCurrentFlashSaleItems();
        return ApiResponse.ok(
                "Current flash sale items retrieved successfully",
                items
        );
    }
}
