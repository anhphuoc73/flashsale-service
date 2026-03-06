package com.flashsale.product.service;

import com.flashsale.product.dto.request.CreateProductRequest;
import com.flashsale.product.dto.request.UpdateProductRequest;
import com.flashsale.product.entity.Product;
import com.flashsale.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product create(CreateProductRequest request) {
        String name = request.getName();
        String description = request.getDescription();
        BigDecimal price = request.getPrice();
        Integer stock = request.getStock();

        if(productRepository.existsByName(name)){
            throw new RuntimeException("Product name already exists");
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setActive(true);

        return productRepository.save(product);
    }

    public  Product update(String productId, UpdateProductRequest request){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Nếu có gửi name và name khác name cũ → check trùng
        if (request.getName() != null
                && !request.getName().equals(product.getName())
                && productRepository.existsByName(request.getName())) {
            throw new RuntimeException("Product name already exists");
        }

        // update từng field
        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if(request.getPrice() != null){
            product.setPrice(request.getPrice());
        }

        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }

        return productRepository.save(product);

    }

    public void delete(String productId){

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product do not found"));

        product.setActive(false);

        productRepository.save(product);
    }

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }


}
