package io.github.lonelyjojos.idempotent.spring;

import java.lang.reflect.Method;

/**
 * 将注解表达式解析为业务幂等键。
 */
public interface IdempotentKeyResolver {

    String resolve(Method method, Object[] arguments, String expression);
}

