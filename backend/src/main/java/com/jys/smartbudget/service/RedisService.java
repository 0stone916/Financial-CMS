package com.jys.smartbudget.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration ACCESS_TOKEN_EXP = Duration.ofMinutes(1);

    private String accessKey(String userId) { return "access:" + userId; }
    private String refreshKey(String userId) { return "refresh:" + userId; }

    public void saveAccessToken(String userId, String token) {
        redisTemplate.opsForValue().set(accessKey(userId), token, ACCESS_TOKEN_EXP);
    }

    public String getAccessToken(String userId) {
        return redisTemplate.opsForValue().get(accessKey(userId));
    }

    public void deleteAccessToken(String userId) {
        redisTemplate.delete(accessKey(userId));
    }

    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(
            refreshKey(userId),
            refreshToken,
            Duration.ofDays(7)
        );
    }

    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get(refreshKey(userId));
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(refreshKey(userId));
    }

        // 락 획득 시도 (30초 동안 유효)
    public boolean acquireLock(String approvalNo) {
        String key = "lock:payment:" + approvalNo;
        // setIfAbsent는 Redis의 SETNX 명령어를 실행합니다 (값이 없을 때만 저장)
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "locked", Duration.ofSeconds(30));
        return success != null && success;
    }

    // 락 해제
    public void releaseLock(String approvalNo) {
        String key = "lock:payment:" + approvalNo;
        redisTemplate.delete(key);
    }
}
