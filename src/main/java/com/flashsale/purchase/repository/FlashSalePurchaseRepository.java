package com.flashsale.purchase.repository;

import com.flashsale.purchase.entity.FlashSalePurchase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface FlashSalePurchaseRepository extends MongoRepository<FlashSalePurchase, String> {
    List<FlashSalePurchase> findByUserId(String userId);

    boolean existsByUserIdAndPurchaseDate(String userId, LocalDate purchaseDate);

    Page<FlashSalePurchase> findByUserId(String userId, Pageable pageable);


}
