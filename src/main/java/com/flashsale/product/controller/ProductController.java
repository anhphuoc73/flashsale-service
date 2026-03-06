package com.flashsale.product.controller;

import com.flashsale.exception.ApiResponse;
import com.flashsale.product.dto.request.CreateProductRequest;
import com.flashsale.product.dto.request.UpdateProductRequest;
import com.flashsale.product.entity.Product;
import com.flashsale.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> create(
            @Valid @RequestBody CreateProductRequest request
    ){
        Product product = productService.create(request);

        return ApiResponse.created(
                "Product created successfully",
                product
        );

    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequest request
    ) {

        Product updatedProduct = productService.update(id, request);
        return ApiResponse.ok(
                "Product updated successfully",
                updatedProduct
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteProduct(
            @PathVariable String id
    ) {

        productService.delete(id);
        Map<String, String> data = new HashMap<>();
        data.put("id", id);

        return ApiResponse.ok(
                "Product deleted successfully",
                data
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productService.getAll(pageable);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("data", productPage.getContent());
        responseData.put("currentPage", productPage.getNumber());
        responseData.put("totalItems", productPage.getTotalElements());
        responseData.put("totalPages", productPage.getTotalPages());

        return ApiResponse.ok("Get product list success", responseData);
    }



}
