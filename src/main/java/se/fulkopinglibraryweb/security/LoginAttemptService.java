package se.fulkopinglibraryweb.security;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = 15 * 60 * 1000; // 15 minutes
    
    private final ConcurrentMap<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> lockoutCache = new ConcurrentHashMap<>();

    public void loginFailed(String key) {
        int attempts = attemptsCache.getOrDefault(key, 0) + 1;
        attemptsCache.put(key, attempts);
        
        if (attempts >= MAX_ATTEMPTS) {
            lockoutCache.put(key, System.currentTimeMillis());
        }
    }

    public void loginSucceeded(String key) {
        attemptsCache.remove(key);
        lockoutCache.remove(key);
    }

    public boolean isBlocked(String key) {
        Long lockoutTime = lockoutCache.get(key);
        if (lockoutTime != null) {
            if (System.currentTimeMillis() - lockoutTime < LOCK_TIME_DURATION) {
                return true;
            }
            lockoutCache.remove(key);
            attemptsCache.remove(key);
        }
        return false;
    }

    public int getRemainingAttempts(String key) {
        return MAX_ATTEMPTS - attemptsCache.getOrDefault(key, 0);
    }
}
