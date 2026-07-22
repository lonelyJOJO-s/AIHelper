package io.github.lonelyjojos.idempotent.spring.redis;

import io.github.lonelyjojos.idempotent.core.AcquireResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers(disabledWithoutDocker = true)
class RedisIdempotentRepositoryTest {

    private static final Duration TTL = Duration.ofMinutes(10);

    @Container
    private static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    private RedisIdempotentRepository repository;

    private StringRedisTemplate redisTemplate;

    private LettuceConnectionFactory connectionFactory;

    @BeforeEach
    void setUp() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(
                REDIS.getHost(), REDIS.getMappedPort(6379));
        connectionFactory = new LettuceConnectionFactory(configuration);
        connectionFactory.afterPropertiesSet();
        redisTemplate = new StringRedisTemplate(connectionFactory);
        redisTemplate.afterPropertiesSet();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
        repository = new RedisIdempotentRepository(redisTemplate);
    }

    @AfterEach
    void tearDown() {
        connectionFactory.destroy();
    }

    @Test
    void shouldRejectSecondAcquireUntilReleased() {
        AcquireResult first = repository.tryAcquire("idempotent:redis:1", TTL);
        AcquireResult second = repository.tryAcquire("idempotent:redis:1", TTL);

        assertTrue(first.acquired());
        assertFalse(second.acquired());

        repository.release("idempotent:redis:1", first.ownerToken());

        assertTrue(repository.tryAcquire("idempotent:redis:1", TTL).acquired());
    }

    @Test
    void shouldRejectAfterSucceededUntilTtlExpires() {
        AcquireResult first = repository.tryAcquire("idempotent:redis:2", TTL);

        repository.markSucceeded("idempotent:redis:2", first.ownerToken(), TTL);

        assertFalse(repository.tryAcquire("idempotent:redis:2", TTL).acquired());
    }

    @Test
    void shouldNotReleaseRecordOwnedByAnotherToken() {
        AcquireResult first = repository.tryAcquire("idempotent:redis:3", TTL);

        repository.release("idempotent:redis:3", first.ownerToken() + "-stale");

        assertFalse(repository.tryAcquire("idempotent:redis:3", TTL).acquired());
    }
}
