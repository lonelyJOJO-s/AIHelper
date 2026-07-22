package io.github.lonelyjojos.idempotent.core;

/**
 * 幂等键抢占结果。
 *
 * @param acquired 是否获得执行权
 * @param ownerToken 本次执行的所有权令牌，未获得执行权时为空
 */
public record AcquireResult(boolean acquired, String ownerToken) {

    public static AcquireResult acquired(String ownerToken) {
        if (ownerToken == null || ownerToken.isBlank()) {
            throw new IllegalArgumentException("ownerToken must not be blank");
        }
        return new AcquireResult(true, ownerToken);
    }

    public static AcquireResult duplicate() {
        return new AcquireResult(false, null);
    }
}
