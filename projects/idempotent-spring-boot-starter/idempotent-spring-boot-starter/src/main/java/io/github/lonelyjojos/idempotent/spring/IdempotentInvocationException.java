package io.github.lonelyjojos.idempotent.spring;

/**
 * 被增强方法抛出了受检异常。
 */
public class IdempotentInvocationException extends RuntimeException {

    public IdempotentInvocationException(Throwable cause) {
        super("idempotent method invocation failed", cause);
    }
}

