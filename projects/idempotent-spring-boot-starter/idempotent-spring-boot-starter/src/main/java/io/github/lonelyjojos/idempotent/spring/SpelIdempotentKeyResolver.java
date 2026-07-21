package io.github.lonelyjojos.idempotent.spring;

import java.lang.reflect.Method;

/**
 * 基于 Spring Expression Language 的幂等键解析器。
 */
public class SpelIdempotentKeyResolver implements IdempotentKeyResolver {

    @Override
    public String resolve(Method method, Object[] arguments, String expression) {
        // TODO learner 将参数名和参数值放入 SpEL 上下文，并校验结果不是空字符串
        throw new UnsupportedOperationException("implement SpEL key resolution");
    }
}

