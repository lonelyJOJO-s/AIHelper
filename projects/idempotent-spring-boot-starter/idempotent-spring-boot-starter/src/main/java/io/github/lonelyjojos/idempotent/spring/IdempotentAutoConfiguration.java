package io.github.lonelyjojos.idempotent.spring;

import io.github.lonelyjojos.idempotent.core.IdempotentExecutor;
import io.github.lonelyjojos.idempotent.core.IdempotentRepository;
import io.github.lonelyjojos.idempotent.core.memory.InMemoryIdempotentRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 幂等组件自动配置。
 */
@AutoConfiguration
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = "idempotent", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IdempotentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdempotentRepository idempotentRepository() {
        return new InMemoryIdempotentRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentExecutor idempotentExecutor(IdempotentRepository repository) {
        return new IdempotentExecutor(repository);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentKeyResolver idempotentKeyResolver() {
        return new SpelIdempotentKeyResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotentAspect idempotentAspect(IdempotentExecutor executor, IdempotentKeyResolver keyResolver) {
        return new IdempotentAspect(executor, keyResolver);
    }
}

