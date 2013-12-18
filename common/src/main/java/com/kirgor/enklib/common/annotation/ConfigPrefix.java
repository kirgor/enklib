package com.kirgor.enklib.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for classes, which are supposed to store configuration values,
 * specifies the prefix of considered system properties names.
 * Used by {@link com.kirgor.enklib.common.ConfigUtils} loadFromSystemProperties method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConfigPrefix {
    String value();
}
