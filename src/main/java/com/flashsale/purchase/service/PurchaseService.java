package com.flashsale.purchase.service;

import com.flashsale.flashsale.dto.response.FlashSaleItemResponse;
import com.flashsale.flashsale.entity.FlashSaleItem;
import com.flashsale.flashsale.repository.FlashSaleItemRepository;
import com.flashsale.purchase.dto.request.PurchaseRequest;
import com.flashsale.purchase.dto.response.PurchaseResponse;
import com.flashsale.purchase.entity.FlashSalePurchase;
import com.flashsale.purchase.repository.FlashSalePurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private  final MongoTemplate mongoTemplate;
    private final FlashSalePurchaseRepository purchaseRepository;

    private final StringRedisTemplate redisTemplate;
    private final FlashSaleItemRepository flashSaleItemRepository;

    public PurchaseResponse purchase(String userId, PurchaseRequest request){

        LocalDate today = LocalDate.now();

        // 1. Check user da mua hom nay chua
        boolean alreadyPurchased = purchaseRepository
                .existsByUserIdAndPurchaseDate(userId, today);

        if(alreadyPurchased){
            throw new RuntimeException("User already purchased today");
        }

        String itemId = request.getFlashSaleItemId();
        String key = "flashsale:item:" + itemId;

        // 2. Lay stock tu redis
        String limitStr = (String) redisTemplate.opsForHash().get(key, "quantityLimit");
        String soldStr = (String) redisTemplate.opsForHash().get(key, "soldQuantity");

        int limit = Integer.parseInt(limitStr);
        int sold = Integer.parseInt(soldStr);

        // 3. Atomic increase soldQuantity
        Long newSold = redisTemplate.opsForHash()
                .increment(key, "soldQuantity", 1);

        if(newSold == null || newSold > limit){
            // rollback
            redisTemplate.opsForHash().increment(key, "soldQuantity", -1);
            throw new RuntimeException("Sold out");
        }

        // 4. Lấy thông tin item từ DB (chỉ đọc)
        FlashSaleItem item = flashSaleItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));


        try{
            // 5. Insert Purchase DB
            FlashSalePurchase purchase = FlashSalePurchase.builder()
                    .userId(userId)
                    .flashSaleItemId(item.getId())
                    .sessionId(item.getSessionId())
                    .purchaseDate(today)
                    .build();

            purchase = purchaseRepository.save(purchase);

            mongoTemplate.updateFirst(
                    new Query(Criteria.where("_id").is(itemId)),
                    new Update().inc("soldQuantity", 1),
                    FlashSaleItem.class
            );

            return PurchaseResponse.builder()
                    .purchaseId(purchase.getId())
                    .flashSaleItemId(item.getId())
                    .sessionId(item.getSessionId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .originalPrice(item.getOriginalPrice())
                    .purchasePrice(item.getSalePrice())
                    .purchaseDate(today)
                    .createdAt(purchase.getCreatedAt())
                    .build();

        }catch (DuplicateKeyException e){
            // rollback Redis stock
            redisTemplate.opsForHash().increment(key, "soldQuantity", -1);

            throw new RuntimeException("User already purchased today");

        }

    }

    public Page<PurchaseResponse> getPurchases(String userId, String role, int page, int size){

        Pageable pageable = PageRequest.of(page, size);

        Page<FlashSalePurchase> purchases;

        if(role.equals("ADMIN")){
            purchases = purchaseRepository.findAll(pageable);
        }else{
            purchases = purchaseRepository.findByUserId(userId, pageable);
        }

        return purchases.map(p ->
                PurchaseResponse.builder()
                        .purchaseId(p.getId())
                        .flashSaleItemId(p.getFlashSaleItemId())
                        .sessionId(p.getSessionId())
                        .purchaseDate(p.getPurchaseDate())
                        .createdAt(p.getCreatedAt())
                        .build()
        );
    }
}
