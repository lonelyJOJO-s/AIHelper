package io.github.lonelyjojos.idempotent.core;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IdempotentCommandTest {

    @Test
    void shouldBuildNamespacedStorageKey() {
        IdempotentCommand command = new IdempotentCommand("create-order", "1001", Duration.ofMinutes(10));

        assertEquals("idempotent:create-order:1001", command.storageKey());
    }

    @Test
    void shouldRejectNonPositiveTtl() {
        assertThrows(IllegalArgumentException.class,
                () -> new IdempotentCommand("create-order", "1001", Duration.ZERO));
    }
}

