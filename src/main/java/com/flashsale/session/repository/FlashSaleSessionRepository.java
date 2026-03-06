package com.flashsale.session.repository;

import com.flashsale.session.entity.FlashSaleSession;
import com.flashsale.session.enums.SessionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashSaleSessionRepository
        extends MongoRepository<FlashSaleSession, String> {

    // Lấy session đang ONGOING
    Optional<FlashSaleSession>
    findFirstByActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDateTime now1,
            LocalDateTime now2
    );

    // Check overlap khi create/update
    List<FlashSaleSession>
    findByActiveTrueAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDateTime end,
            LocalDateTime start
    );

    Optional<FlashSaleSession>
    findFirstByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            LocalDateTime start,
            LocalDateTime end
    );
    Optional<FlashSaleSession> findFirstByStartTimeLessThanEqualAndEndTimeGreaterThanEqualAndActiveTrue(
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<FlashSaleSession> findByIdAndActiveTrue(String id);
}
