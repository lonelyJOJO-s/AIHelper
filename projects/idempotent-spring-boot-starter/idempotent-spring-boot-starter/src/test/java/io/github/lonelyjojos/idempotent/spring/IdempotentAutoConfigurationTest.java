package io.github.lonelyjojos.idempotent.spring;

import io.github.lonelyjojos.idempotent.core.IdempotentExecutor;
import io.github.lonelyjojos.idempotent.core.IdempotentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class IdempotentAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IdempotentAutoConfiguration.class));

    @Test
    void shouldRegisterDefaultBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(IdempotentRepository.class);
            assertThat(context).hasSingleBean(IdempotentExecutor.class);
            assertThat(context).hasSingleBean(IdempotentKeyResolver.class);
            assertThat(context).hasSingleBean(IdempotentAspect.class);
        });
    }
}
