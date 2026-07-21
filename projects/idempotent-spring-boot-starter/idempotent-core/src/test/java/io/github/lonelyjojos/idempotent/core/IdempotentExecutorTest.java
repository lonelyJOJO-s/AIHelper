package io.github.lonelyjojos.idempotent.core;

import io.github.lonelyjojos.idempotent.core.memory.InMemoryIdempotentRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdempotentExecutorTest {

    @Disabled("TODO learner 完成 IdempotentExecutor 后启用")
    @Test
    void shouldExecuteActionOnlyOnceForSameKey() {
        IdempotentExecutor executor = new IdempotentExecutor(new InMemoryIdempotentRepository());
        IdempotentCommand command = new IdempotentCommand("create-order", "1001", Duration.ofMinutes(10));
        AtomicInteger executions = new AtomicInteger();

        assertEquals("ok", executor.execute(command, () -> {
            executions.incrementAndGet();
            return "ok";
        }));
        assertThrows(DuplicateExecutionException.class,
                () -> executor.execute(command, executions::incrementAndGet));
        assertEquals(1, executions.get());
    }

    @Disabled("TODO learner 完成失败释放策略后启用")
    @Test
    void shouldAllowRetryAfterActionFailure() {
        IdempotentExecutor executor = new IdempotentExecutor(new InMemoryIdempotentRepository());
        IdempotentCommand command = new IdempotentCommand("create-order", "1002", Duration.ofMinutes(10));

        assertThrows(IllegalStateException.class,
                () -> executor.execute(command, () -> {
                    throw new IllegalStateException("business failed");
                }));
        assertEquals("retried", executor.execute(command, () -> "retried"));
    }
}

