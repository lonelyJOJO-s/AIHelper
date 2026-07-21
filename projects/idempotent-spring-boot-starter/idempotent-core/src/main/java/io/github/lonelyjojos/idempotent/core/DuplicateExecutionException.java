package io.github.lonelyjojos.idempotent.core;

/**
 * 相同幂等键已被执行或正在执行。
 */
public class DuplicateExecutionException extends RuntimeException {

    public DuplicateExecutionException(String storageKey) {
        super("duplicate execution, storageKey=" + storageKey);
    }
}

