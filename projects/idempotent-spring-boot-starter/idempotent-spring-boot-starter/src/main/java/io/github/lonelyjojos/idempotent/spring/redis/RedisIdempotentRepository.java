package io.github.lonelyjojos.idempotent.spring.redis;

import io.github.lonelyjojos.idempotent.core.AcquireResult;
import io.github.lonelyjojos.idempotent.core.IdempotentRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Redis-backed idempotent repository.
 */
public class RedisIdempotentRepository implements IdempotentRepository {

    private static final String RUNNING = "RUNNING";

    private static final String SUCCEEDED = "SUCCEEDED";

    private static final String VALUE_SEPARATOR = "|";

    private static final RedisScript<Long> TRY_ACQUIRE_SCRIPT = RedisScript.of("""
            if redis.call('EXISTS', KEYS[1]) == 0 then
                redis.call('PSETEX', KEYS[1], ARGV[2], ARGV[1])
                return 1
            end
            return 0
            """, Long.class);

    private static final RedisScript<Long> MARK_SUCCEEDED_SCRIPT = RedisScript.of("""
            local value = redis.call('GET', KEYS[1])
            if not value then
                return 0
            end
            local separator = string.find(value, '|', 1, true)
            if not separator then
                return 0
            end
            local ownerToken = string.sub(value, 1, separator - 1)
            if ownerToken ~= ARGV[1] then
                return 0
            end
            redis.call('PSETEX', KEYS[1], ARGV[3], ARGV[2])
            return 1
            """, Long.class);

    private static final RedisScript<Long> RELEASE_SCRIPT = RedisScript.of("""
            local value = redis.call('GET', KEYS[1])
            if not value then
                return 0
            end
            local separator = string.find(value, '|', 1, true)
            if not separator then
                return 0
            end
            local ownerToken = string.sub(value, 1, separator - 1)
            if ownerToken ~= ARGV[1] then
                return 0
            end
            return redis.call('DEL', KEYS[1])
            """, Long.class);

    private final StringRedisTemplate redisTemplate;

    public RedisIdempotentRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = Objects.requireNonNull(redisTemplate, "redisTemplate must not be null");
    }

    @Override
    public AcquireResult tryAcquire(String storageKey, Duration ttl) {
        String ownerToken = UUID.randomUUID().toString();
        Long acquired = redisTemplate.execute(
                TRY_ACQUIRE_SCRIPT,
                key(storageKey),
                value(ownerToken, RUNNING),
                ttlMillis(ttl));
        if (Objects.equals(acquired, 1L)) {
            return AcquireResult.acquired(ownerToken);
        }
        return AcquireResult.duplicate();
    }

    @Override
    public void markSucceeded(String storageKey, String ownerToken, Duration ttl) {
        Objects.requireNonNull(ownerToken, "ownerToken must not be null");
        redisTemplate.execute(
                MARK_SUCCEEDED_SCRIPT,
                key(storageKey),
                ownerToken,
                value(ownerToken, SUCCEEDED),
                ttlMillis(ttl));
    }

    @Override
    public void release(String storageKey, String ownerToken) {
        Objects.requireNonNull(ownerToken, "ownerToken must not be null");
        redisTemplate.execute(RELEASE_SCRIPT, key(storageKey), ownerToken);
    }

    private static List<String> key(String storageKey) {
        Objects.requireNonNull(storageKey, "storageKey must not be null");
        return Collections.singletonList(storageKey);
    }

    private static String value(String ownerToken, String status) {
        return ownerToken + VALUE_SEPARATOR + status;
    }

    private static String ttlMillis(Duration ttl) {
        Objects.requireNonNull(ttl, "ttl must not be null");
        if (ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("ttl must be positive");
        }
        long millis = Math.max(1L, ttl.toMillis());
        return Long.toString(millis);
    }
}
