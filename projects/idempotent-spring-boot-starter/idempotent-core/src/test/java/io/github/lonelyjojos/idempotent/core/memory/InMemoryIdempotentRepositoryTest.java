package io.github.lonelyjojos.idempotent.core.memory;

import io.github.lonelyjojos.idempotent.core.AcquireResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryIdempotentRepositoryTest {

    private static final Duration TTL = Duration.ofMinutes(10);

    @Disabled("TODO learner 完成 InMemoryIdempotentRepository 后启用")
    @Test
    void shouldRejectSecondAcquireUntilReleased() {
        InMemoryIdempotentRepository repository = new InMemoryIdempotentRepository();

        AcquireResult first = repository.tryAcquire("idempotent:test:1", TTL);
        AcquireResult second = repository.tryAcquire("idempotent:test:1", TTL);

        assertTrue(first.acquired());
        assertFalse(second.acquired());

        repository.release("idempotent:test:1", first.ownerToken());

        assertTrue(repository.tryAcquire("idempotent:test:1", TTL).acquired());
    }
}
