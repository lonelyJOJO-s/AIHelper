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
        // TODO learner 完成抢占、重复判断、业务执行以及成功或失败后的状态收尾
        throw new UnsupportedOperationException("implement IdempotentExecutor.execute");
    }
}

