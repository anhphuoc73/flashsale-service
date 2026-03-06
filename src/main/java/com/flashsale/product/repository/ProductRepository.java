package com.flashsale.product.repository;

import com.flashsale.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    //Find by name
    Optional<Product> findByName(String name);

    // Check name
    boolean existsByName(String name);

    // Get product active
    java.util.List<Product> findByActiveTrue();

    Page<Product> findByActiveTrue(Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(String id);
}
