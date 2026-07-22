package io.github.lonelyjojos.idempotent.spring;

import io.github.lonelyjojos.idempotent.core.IdempotentExecutor;
import io.github.lonelyjojos.idempotent.core.IdempotentRepository;
import io.github.lonelyjojos.idempotent.core.memory.InMemoryIdempotentRepository;
import io.github.lonelyjojos.idempotent.spring.redis.RedisIdempotentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotentAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IdempotentAutoConfiguration.class));

    @Test
    void shouldRegisterDefaultBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(IdempotentRepository.class);
            assertThat(context).hasSingleBean(InMemoryIdempotentRepository.class);
            assertThat(context).hasSingleBean(IdempotentExecutor.class);
            assertThat(context).hasSingleBean(IdempotentKeyResolver.class);
            assertThat(context).hasSingleBean(IdempotentAspect.class);
        });
    }

    @Test
    void shouldRegisterRedisRepositoryWhenConfigured() {
        contextRunner
                .withPropertyValues("idempotent.repository=redis")
                .withBean(StringRedisTemplate.class, this::redisTemplate)
                .run(context -> {
                    assertThat(context).hasSingleBean(IdempotentRepository.class);
                    assertThat(context).hasSingleBean(RedisIdempotentRepository.class);
                    assertThat(context).doesNotHaveBean(InMemoryIdempotentRepository.class);
                });
    }

    private StringRedisTemplate redisTemplate() {
        return new StringRedisTemplate(new FakeRedisConnectionFactory());
    }

    private static final class FakeRedisConnectionFactory implements RedisConnectionFactory {

        @Override
        public boolean getConvertPipelineAndTxResults() {
            return false;
        }

        @Override
        public RedisConnection getConnection() {
            return null;
        }

        @Override
        public RedisClusterConnection getClusterConnection() {
            return null;
        }

        @Override
        public RedisSentinelConnection getSentinelConnection() {
            return null;
        }

        @Override
        public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
            return null;
        }
    }
}
