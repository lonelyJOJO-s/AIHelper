package io.github.lonelyjojos.idempotent.core.memory;

import io.github.lonelyjojos.idempotent.core.AcquireResult;
import io.github.lonelyjojos.idempotent.core.IdempotentRepository;
import io.github.lonelyjojos.idempotent.core.entity.Entry;
import io.github.lonelyjojos.idempotent.core.enums.Status;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 单进程内存幂等仓储。
 * @author Joey
 */
public class InMemoryIdempotentRepository implements IdempotentRepository {

    private final Clock clock;

    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

    public InMemoryIdempotentRepository() {
        this(Clock.systemUTC());
    }

    InMemoryIdempotentRepository(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
    }

    @Override
    public AcquireResult tryAcquire(String storageKey, Duration ttl) {
        AtomicReference<AcquireResult> resultRef = new AtomicReference<>();
        entries.compute(storageKey, (key, entry) -> {
            Instant now = clock.instant();
            if (entry == null || entry.isExpired(now)) {
                Instant expireAt = now.plus(ttl);
                String ownerToken = UUID.randomUUID().toString();
                resultRef.set(AcquireResult.acquired(ownerToken));
                return new Entry(ownerToken, Status.RUNNING, expireAt);
            }
            resultRef.set(AcquireResult.duplicate());
            return entry;
        });
        return resultRef.get();
    }

    @Override
    public void markSucceeded(String storageKey, String ownerToken, Duration ttl) {
        entries.computeIfPresent(storageKey, (key, entry) -> {
            if (!entry.ownerToken().equals(ownerToken)) {
                return entry;
            }
            return entry.markSucceeded(clock.instant().plus(ttl));
        });
    }

    @Override
    public void release(String storageKey, String ownerToken) {
        entries.computeIfPresent(storageKey, (key, entry) -> {
            if (!entry.ownerToken().equals(ownerToken)) {
                return entry;
            }
            return null;
        });
    }

    Clock clock() {
        return clock;
    }
}
