package com.SmartRatelimiterAndAbuseDetector.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AbuseDetectionService {

    private final StringRedisTemplate redisTemplate;

    public AbuseDetectionService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isBlocked(String clientId){
        return Boolean.TRUE.equals(
                redisTemplate.hasKey("block:"+clientId)
        );
    }

    public boolean isRapidFire(String clientId) {
        String key = "abuse:burst:" + clientId;
        long now = System.currentTimeMillis();

        String last = redisTemplate.opsForValue().get(key);
        redisTemplate.opsForValue()
                .set(key, String.valueOf(now), Duration.ofSeconds(2));

        if (last == null) return false;
        return now - Long.parseLong(last) < 50;
    }

    public void recordError(String clientId) {
        String key = "abuse:error:" + clientId;
        Long count = redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(1));

        if (count != null && count >= 10) {
            block(clientId, Duration.ofMinutes(15));
        }
    }

    public void block(String clientId, Duration duration) {
        redisTemplate.opsForValue()
                .set("block:" + clientId, "BLOCKED", duration);
    }

}
