package com.flashsale.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "blacklist:";

    //Lưu token vào blacklist với thời gian còn lại của token sau khi logout
    public void blacklistToken(String token, long expirationMillis){
        System.out.println(">>> blacklistToken CALLED");
        System.out.println(">>> ExpirationMillis: " + expirationMillis);
        if(expirationMillis <= 0 ){
            System.out.println(">>> SKIP because expiration <= 0");
            return;
        }

        String key = PREFIX + token;
        System.out.println(">>> Saving key: " + key);

        redisTemplate.opsForValue().set(
            key,
            "blacklisted",
            Duration.ofMillis(expirationMillis)
        );
        System.out.println(">>> Saved to redis");

    }

    // Kiểm tra token có bị blacklist không
    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(
                redisTemplate.hasKey(PREFIX + token)
        );
    }

}
