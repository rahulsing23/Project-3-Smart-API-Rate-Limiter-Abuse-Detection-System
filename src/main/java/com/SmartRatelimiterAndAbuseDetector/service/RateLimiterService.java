package com.SmartRatelimiterAndAbuseDetector.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.Collection;
import java.util.Collections;

@Service
public class RateLimiterService {

    private static final int CAPACITY = 10;
    private static final double REFILL_RATE_PER_MILLIS  = 10.0 / 60000.0;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    public RateLimiterService(StringRedisTemplate redisTemplate) throws Exception {
        this.redisTemplate = redisTemplate;
        this.script = new DefaultRedisScript<>();
        this.script.setResultType(Long.class);
        this.script.setScriptText(
                Files.readString(
                        new ClassPathResource("rate_limiter.lua")
                                .getFile()
                                .toPath()
                )
        );
    }

    public boolean allowRequest(String clientId){
        Long result = redisTemplate.execute(
                script,
                Collections.singletonList("rate_limit:"+clientId),
                String.valueOf(CAPACITY),
                String.valueOf(REFILL_RATE_PER_MILLIS),
                String.valueOf(System.currentTimeMillis())

        );
        return result !=null && result == 1;
    }

}
