package io.github.lonelyjojos.idempotent.core;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * 编排一次幂等执行的核心入口。
 */
public class IdempotentExecutor {

    private final IdempotentRepository repository;

    public IdempotentExecutor(IdempotentRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public <T> T execute(IdempotentCommand command, Supplier<T> action) {
        AcquireResult result = repository.tryAcquire(command.storageKey(), command.ttl());
        if (!result.acquired()) {
            throw new DuplicateExecutionException(command.storageKey());
        }
        try {
            T value = action.get();
            repository.markSucceeded(command.storageKey(), result.ownerToken(), command.ttl());
            return value;
        } catch (RuntimeException | Error e) {
            repository.release(command.storageKey(), result.ownerToken());
            throw e;
        }
    }
}

