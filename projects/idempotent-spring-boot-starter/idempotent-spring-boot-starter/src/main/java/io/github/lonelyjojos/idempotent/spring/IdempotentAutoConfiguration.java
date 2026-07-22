package io.github.lonelyjojos.idempotent.spring;

import io.github.lonelyjojos.idempotent.core.IdempotentExecutor;
import io.github.lonelyjojos.idempotent.core.IdempotentRepository;
import io.github.lonelyjojos.idempotent.core.memory.InMemoryIdempotentRepository;
import io.github.lonelyjojos.idempotent.spring.redis.RedisIdempotentRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 幂等组件自动配置。
 */
@AutoConfiguration
@EnableConfigurationProperties(IdempotentProperties.class)
@ConditionalOnProperty(prefix = "idempotent", name = "enabled", havingValue = "true", matchIfMissing = true)
public class IdempotentAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(StringRedisTemplate.class)
    @ConditionalOnProperty(prefix = "idempotent", name = "repository", havingValue = "redis")
    public IdempotentRepository redisIdempotentRepository(StringRedisTemplate redisTemplate) {
        return new RedisIdempotentRepository(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "idempotent", name = "repository", havingValue = "memory", matchIfMissing = true)
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
