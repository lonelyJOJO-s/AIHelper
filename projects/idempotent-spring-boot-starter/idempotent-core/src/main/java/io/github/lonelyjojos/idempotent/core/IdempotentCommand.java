package io.github.lonelyjojos.idempotent.core;

import java.time.Duration;
import java.util.Objects;

/**
 * 一次幂等执行所需的不可变参数。
 *
 * @param namespace 业务命名空间
 * @param key 业务幂等键
 * @param ttl 幂等记录有效期
 */
public record IdempotentCommand(String namespace, String key, Duration ttl) {

    public IdempotentCommand {
        if (namespace == null || namespace.isBlank()) {
            throw new IllegalArgumentException("namespace must not be blank");
        }
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
        Objects.requireNonNull(ttl, "ttl must not be null");
        if (ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("ttl must be positive");
        }
    }

    public String storageKey() {
        return "idempotent:" + namespace + ":" + key;
    }
}

