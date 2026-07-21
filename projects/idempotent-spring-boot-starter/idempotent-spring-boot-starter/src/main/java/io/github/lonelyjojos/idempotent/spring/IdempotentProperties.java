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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

