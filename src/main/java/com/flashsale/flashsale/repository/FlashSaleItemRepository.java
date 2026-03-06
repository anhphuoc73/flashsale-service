package com.flashsale.flashsale.repository;

import com.flashsale.flashsale.dto.response.FlashSaleItemResponse;
import com.flashsale.flashsale.entity.FlashSaleItem;
import com.flashsale.session.entity.FlashSaleSession;
import com.flashsale.session.enums.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface FlashSaleItemRepository extends MongoRepository<FlashSaleItem, String>{

    // Lay tat ca item theo session
    List<FlashSaleItem> findBySessionId(String sessionId);

    // Lay 1 item theo id va session
    Optional<FlashSaleItem> findByIdAndSessionId(String id, String sessionId);

    boolean existsBySessionIdAndProductId(String sessionId, String productId);

    Page<FlashSaleItem> findBySessionId(String sessionId, Pageable pageable);

    Page<FlashSaleItem> findByProductId(String productId, Pageable pageable);

    Page<FlashSaleItem> findBySessionIdAndProductId(
            String sessionId,
            String productId,
            Pageable pageable
    );

    boolean existsBySessionId(String sessionId);

}
