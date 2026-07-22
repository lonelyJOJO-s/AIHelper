package io.github.lonelyjojos.idempotent.spring;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * 基于 Spring Expression Language 的幂等键解析器。
 * @author jianheng.zhu
 */
public class SpelIdempotentKeyResolver implements IdempotentKeyResolver {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public String resolve(Method method, Object[] arguments, String expression) {
        Objects.requireNonNull(method, "method must not be null");
        Objects.requireNonNull(arguments, "arguments must not be null");
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("expression must not be blank");
        }

        StandardEvaluationContext context = new StandardEvaluationContext();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            context.setVariable(parameters[i].getName(), arguments[i]);
        }

        Object value = parser.parseExpression(expression).getValue(context);
        String key = Objects.toString(value, "");
        if (key.isBlank()) {
            throw new IllegalArgumentException("idempotent key must not be blank");
        }
        return key;
    }
}
