package io.github.lonelyjojos.idempotent.core.entity;

import io.github.lonelyjojos.idempotent.core.enums.Status;

import java.time.Instant;

/**
 * @author jianheng.zhu
 * @description
 * @date 21/07/26
 */
public record Entry (
    String ownerToken,
    Status status,
    Instant expiresAt
){
    public boolean isExpired(Instant now) {
        return now.isAfter(expiresAt);
    }
    public Entry markSucceeded(Instant expireAt) {
        return new Entry(ownerToken, Status.SUCCEEDED, expireAt);
    }
}
