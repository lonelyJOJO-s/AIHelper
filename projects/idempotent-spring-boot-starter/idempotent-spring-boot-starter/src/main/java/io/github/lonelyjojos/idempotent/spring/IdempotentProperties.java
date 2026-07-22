package io.github.lonelyjojos.idempotent.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 幂等组件配置。
 */
@ConfigurationProperties("idempotent")
public class IdempotentProperties {

    /**
     * 是否启用幂等组件。
     */
    private boolean enabled = true;

    /**
     * 幂等状态仓储类型：memory 或 redis。
     */
    private String repository = "memory";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }
}
