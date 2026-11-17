package com.xbf8.secure_s3.session;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.xbf8.secure_s3.session.dto.SessionRedisValue;
import com.xbf8.secure_s3.session.dto.SessionResponse;
import com.xbf8.secure_s3.session.enums.SessionOperation;
import com.xbf8.secure_s3.utils.Hasher;

@Service
public class SessionService {
    private static final String REDIS_SESSION_PREFIX_KEY = "ss3:sessions";

    private final Integer sessionTTLMinutes;
    private final RedisTemplate<String, Object> redisTemplate;
    public SessionService(
        @Value("${secure_s3.session.redis.ttl}") Integer sessionTTLMinutes,
        final RedisTemplate<String, Object> redisTemplate
    ) {
        this.sessionTTLMinutes = sessionTTLMinutes;
        this.redisTemplate = redisTemplate;
    }

    private String formatRedisKey(String key) {
        return REDIS_SESSION_PREFIX_KEY + ":" + key;
    }

    public SessionResponse create(long fileId, SessionOperation operation) {
        Optional<String> key = SessionKey.generate(fileId);
        if (key.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Optional<String> hashedKey = Hasher.sha256ToBase64URL(key.get());
        if (hashedKey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SessionRedisValue value = new SessionRedisValue(fileId, operation);
        this.redisTemplate.opsForValue().set(
            this.formatRedisKey(hashedKey.get()),
            value,
            Duration.ofMinutes(this.sessionTTLMinutes)
        );

        return new SessionResponse(fileId, operation, key.get());
    }

    public Optional<SessionRedisValue> get(String key) {
        if (key.length() < 43) {
            return Optional.empty();
        }

        Optional<String> hashedKey = Hasher.sha256ToBase64URL(key);
        if (hashedKey.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        SessionRedisValue value = (SessionRedisValue) this.redisTemplate.opsForValue().get(
            this.formatRedisKey(hashedKey.get())
        );

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(value);
    }
}
