package io.github.lonelyjojos.idempotent.spring;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpelIdempotentKeyResolverTest {

    @Disabled("TODO learner 完成 SpelIdempotentKeyResolver 后启用")
    @Test
    void shouldResolveMethodArgumentByName() throws NoSuchMethodException {
        Method method = SampleService.class.getDeclaredMethod("create", String.class);
        SpelIdempotentKeyResolver resolver = new SpelIdempotentKeyResolver();

        assertEquals("order-1001", resolver.resolve(method, new Object[]{"order-1001"}, "#requestId"));
    }

    private static final class SampleService {

        @SuppressWarnings("unused")
        public void create(String requestId) {
        }
    }
}

