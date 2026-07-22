package io.github.lonelyjojos.idempotent.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明方法需要进行幂等控制。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Idempotent {

    String key();

    String namespace() default "default";

    long ttlSeconds() default 600;
}

