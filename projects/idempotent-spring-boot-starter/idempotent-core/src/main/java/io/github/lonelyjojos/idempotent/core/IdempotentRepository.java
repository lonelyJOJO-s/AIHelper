package io.github.lonelyjojos.idempotent.core;

import java.time.Duration;

/**
 * 幂等状态存储契约。
 */
public interface IdempotentRepository {

    AcquireResult tryAcquire(String storageKey, Duration ttl);

    void markSucceeded(String storageKey, String ownerToken, Duration ttl);

    void release(String storageKey, String ownerToken);
}
