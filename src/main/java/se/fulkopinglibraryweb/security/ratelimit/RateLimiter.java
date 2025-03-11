package se.fulkopinglibraryweb.security.ratelimit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiter {
    public static final int TOO_MANY_REQUESTS = 429;
    private final int maxRequests;
    private final int timeWindowSeconds;
    private final int blockDurationSeconds;
    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiter(RedisTemplate<String, String> redisTemplate,
                      int maxRequests, 
                      int timeWindowSeconds,
                      int blockDurationSeconds) {
        this.redisTemplate = redisTemplate;
        this.maxRequests = maxRequests;
        this.timeWindowSeconds = timeWindowSeconds;
        this.blockDurationSeconds = blockDurationSeconds;
    }

    public boolean tryAcquire(String ipAddress) {
        String key = "rate_limit:" + ipAddress;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        
        String currentCount = ops.get(key);
        if (currentCount == null) {
            ops.set(key, "1", Duration.ofSeconds(timeWindowSeconds));
            return true;
        }

        int count = Integer.parseInt(currentCount);
        if (count >= maxRequests) {
            // Block the IP for block duration
            ops.set(key, String.valueOf(count), blockDurationSeconds, TimeUnit.SECONDS);
            return false;
        }

        ops.increment(key);
        return true;
    }

    public boolean allowRequest(String ipAddress) {
        return tryAcquire(ipAddress);
    }
}
