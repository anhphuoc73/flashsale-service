package com.flashsale.flashsale.service;

import com.flashsale.flashsale.dto.request.CreateFlashSaleItemRequest;
import com.flashsale.flashsale.dto.request.UpdateFlashSaleItemRequest;
import com.flashsale.flashsale.dto.response.FlashSaleItemResponse;
import com.flashsale.flashsale.dto.response.FlashSaleItemUserResponse;
import com.flashsale.flashsale.entity.FlashSaleItem;
import com.flashsale.flashsale.enums.FlashSaleItemStatus;
import com.flashsale.flashsale.repository.FlashSaleItemRepository;
import com.flashsale.product.entity.Product;
import com.flashsale.product.repository.ProductRepository;
import com.flashsale.session.dto.request.UpdateFlashSaleSessionRequest;
import com.flashsale.session.entity.FlashSaleSession;
import com.flashsale.session.enums.SessionStatus;
import com.flashsale.session.repository.FlashSaleSessionRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FlashSaleItemService {

    private final FlashSaleItemRepository repository;
    private final ProductRepository productRepository;
    private final FlashSaleSessionRepository sessionRepository;

    private final StringRedisTemplate redisTemplate;

    public FlashSaleItemResponse create(CreateFlashSaleItemRequest request){

        // 1. Check session ton tai và có thêm điều kiện Active la true
        FlashSaleSession session = sessionRepository.findByIdAndActiveTrue(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session do not found"));

        LocalDateTime now = LocalDateTime.now();

        // 2. Khong cho chinh neu sesssion da bat dau
        if(session.getStartTime().isBefore(now)){
            throw new RuntimeException("Cannot modify session that already started");
        }

        // 3. Khong cho neu session da ket thuc
        if(session.getEndTime() != null && session.getEndTime().isBefore(now)){
            throw new RuntimeException("Session already ended");
        }

        // 4. Check product ton tai
        Product product = productRepository.findByIdAndActiveTrue(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product do not found"));

        // 5. Khong cho trung product trong session
        if(repository.existsBySessionIdAndProductId(
                request.getSessionId(),
                request.getProductId()
        )){
            throw new RuntimeException("Product already exists in this session");
        }

        // 6. Validate business rule
        if(request.getQuantityLimit() <= 0){
            throw new RuntimeException("Quantity limit must be greater than 0");
        }

        if (request.getSalePrice()
                .compareTo(product.getPrice()) >= 0) {
            throw new RuntimeException("Sale price must be less than original price");
        }

        // 7. tao entity
        FlashSaleItem item = new FlashSaleItem();
        item.setSessionId(session.getId());
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setOriginalPrice(product.getPrice());
        item.setProductDescription(product.getDescription());
        item.setSalePrice(request.getSalePrice());
        item.setQuantityLimit(request.getQuantityLimit());
        item.setSoldQuantity(0);
        item.setActive(true);

        FlashSaleItem saved = repository.save(item);

        // 8 . Ghi vao redis
        saveFlashSaleItemToRedis(saved, session);


        // 9. Map response
        return FlashSaleItemResponse.builder()
                .id(saved.getId())
                .sessionId(saved.getSessionId())
                .productId(saved.getProductId())
                .productName(saved.getProductName())
                .originalPrice(saved.getOriginalPrice())
                .productDescription(saved.getProductDescription())
                .salePrice(saved.getSalePrice())
                .quantityLimit(saved.getQuantityLimit())
                .soldQuantity(saved.getSoldQuantity())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();

    }
    private void saveFlashSaleItemToRedis(FlashSaleItem item, FlashSaleSession session) {

        String key = "flashsale:item:" + item.getId();

        redisTemplate.opsForHash().put(key, "quantityLimit", String.valueOf(item.getQuantityLimit()));
        redisTemplate.opsForHash().put(key, "soldQuantity", String.valueOf(item.getSoldQuantity()));

        Date expireAt = Date.from(
                session.getEndTime()
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        redisTemplate.expireAt(key, expireAt);
    }

    public FlashSaleItemResponse update(String id, UpdateFlashSaleItemRequest request){

        FlashSaleItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));

        // update salePrice
        if(request.getSalePrice() != null){
            item.setSalePrice(request.getSalePrice());
        }

        // update quantityLimit
        if(request.getQuantityLimit() != null){

            // Khong duoc nho hon so da ban
            if(request.getQuantityLimit() < item.getSoldQuantity()){
                throw new RuntimeException("Quantity limit cannot be less than sold quantity");
            }
            item.setQuantityLimit(request.getQuantityLimit());
        }

        FlashSaleItem saved = repository.save(item);

        return FlashSaleItemResponse.builder()
                .id(saved.getId())
                .sessionId(saved.getSessionId())
                .productId(saved.getProductId())
                .salePrice(saved.getSalePrice())
                .quantityLimit(saved.getQuantityLimit())
                .soldQuantity(saved.getSoldQuantity())
                .createdAt(saved.getCreatedAt())
                .updatedAt(saved.getUpdatedAt())
                .build();
    }

    public void delete(String id) {

        FlashSaleItem item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flash sale item not found"));

        if (!item.isActive()) {
            throw new RuntimeException("Flash sale item already deleted");
        }

        FlashSaleSession session = sessionRepository.findById(item.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = session.getStartTime();

        // Nếu thời điểm hiện tại >= thời điểm bắt đầu
        if (now.isAfter(startTime) || now.isEqual(startTime)) {
            throw new RuntimeException("Cannot delete item because session already started");
        }

        item.setActive(false);

        // delete REDIS
        String key = "flashsale:item:" + item.getId();
        redisTemplate.delete(key);

        repository.save(item);
    }

    public Page<FlashSaleItemResponse> getAdminItems(
            String sessionId,
            String productId,
            Pageable pageable
    ) {

        Page<FlashSaleItem> page;

        if (sessionId != null && productId != null) {
            page = repository.findBySessionIdAndProductId(sessionId, productId, pageable);
        } else if (sessionId != null) {
            page = repository.findBySessionId(sessionId, pageable);
        } else if (productId != null) {
            page = repository.findByProductId(productId, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        LocalDateTime now = LocalDateTime.now();

        return page.map(item -> {

            FlashSaleSession session = sessionRepository
                    .findById(item.getSessionId())
                    .orElse(null);

            return FlashSaleItemResponse.builder()
                    .id(item.getId())
                    .sessionId(item.getSessionId())
                    .productId(item.getProductId())
                    .productName(item.getProductName())
                    .originalPrice(item.getOriginalPrice())
                    .productDescription(item.getProductDescription())
                    .salePrice(item.getSalePrice())
                    .quantityLimit(item.getQuantityLimit())
                    .soldQuantity(item.getSoldQuantity())
                    .active(item.isActive())
                    .createdAt(item.getCreatedAt())
                    .updatedAt(item.getUpdatedAt())
                    .status(resolveStatus(item, session, now))
                    .build();
        });
    }

    private FlashSaleItemStatus resolveStatus(
            FlashSaleItem item,
            FlashSaleSession session,
            LocalDateTime now
    ) {

        if (!item.isActive()) {
            return FlashSaleItemStatus.DELETED;
        }

        if (session == null) {
            return FlashSaleItemStatus.INVALID_SESSION;
        }

        if (item.getSoldQuantity() >= item.getQuantityLimit()) {
            return FlashSaleItemStatus.SOLD_OUT;
        }

        if (now.isBefore(session.getStartTime())) {
            return FlashSaleItemStatus.UPCOMING;
        }

        if (session.getEndTime() != null &&
                now.isAfter(session.getEndTime())) {
            return FlashSaleItemStatus.EXPIRED;
        }

        return FlashSaleItemStatus.ACTIVE;
    }


    public List<FlashSaleItemUserResponse> getCurrentFlashSaleItems(){

        // 1. Tim session dang dien ra
        // Dieu kien: - startTime <= now; endTime >= now;
        LocalDateTime now = LocalDateTime.now();

        FlashSaleSession session = sessionRepository
                .findFirstByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndActiveTrue(
                        now,
                        now
                )
                .orElse(null);

        // Neu khong co session nao dang dien ra -> tra ve danh sach rong
        if(session == null){
            return java.util.Collections.emptyList();
        }

        // 2. Lay tat ca item cua session
        // chi lay item dang active
        List<FlashSaleItem> items = repository.findBySessionId(session.getId())
                .stream()
                .filter(FlashSaleItem::isActive)
                .toList();

        if(items.isEmpty()){
            return java.util.Collections.emptyList();
        }

        // 3. lay tat ca product trong 1 lan query
        List<String> productIds = items.stream()
                .map(FlashSaleItem::getProductId)
                .toList();

        Map<String, Product> productMap =
                productRepository.findAllById(productIds)
                        .stream()
                        .collect(java.util.stream.Collectors.toMap(
                                Product::getId,
                                p -> p
                        ));

        // 4. Filter con hang & map sang reponse
        // Dk con hang: quantityLimit > soldQuantity
        return items.stream()
                .map(item -> {

                    Product product = productMap.get(item.getProductId());

                    int remaining =
                            item.getQuantityLimit() - item.getSoldQuantity();

                    return FlashSaleItemUserResponse.builder()
                            .id(item.getId())
                            .sessionId(session.getId())        // thêm
                            .sessionName(session.getName())
                            .productId(item.getProductId())
                            .productName(
                                    product != null ? product.getName() : null
                            )
                            .originalPrice(
                                    product != null ? product.getPrice() : null
                            )
                            .salePrice(item.getSalePrice())
                            .remainingQuantity(remaining)
                            .soldOut(remaining <= 0)
                            .build();
                })
                .toList();

    }


}
