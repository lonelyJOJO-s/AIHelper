package io.github.lonelyjojos.idempotent.spring;

import io.github.lonelyjojos.idempotent.core.IdempotentCommand;
import io.github.lonelyjojos.idempotent.core.IdempotentExecutor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Objects;

/**
 * 将 Spring 方法调用转换为核心幂等执行命令。
 */
@Aspect
public class IdempotentAspect {

    private final IdempotentExecutor executor;
    private final IdempotentKeyResolver keyResolver;

    public IdempotentAspect(IdempotentExecutor executor, IdempotentKeyResolver keyResolver) {
        this.executor = Objects.requireNonNull(executor);
        this.keyResolver = Objects.requireNonNull(keyResolver);
    }

    @Around("@annotation(annotation)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent annotation) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String key = keyResolver.resolve(method, joinPoint.getArgs(), annotation.key());
        IdempotentCommand command = new IdempotentCommand(
                annotation.namespace(), key, Duration.ofSeconds(annotation.ttlSeconds()));
        return executor.execute(command, () -> proceed(joinPoint));
    }

    private Object proceed(ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Throwable throwable) {
            throw new IdempotentInvocationException(throwable);
        }
    }
}

