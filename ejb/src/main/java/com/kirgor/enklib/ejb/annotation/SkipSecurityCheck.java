package com.kirgor.enklib.ejb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for API methods, which will allow invocation without authentication.
 * Authentication process will be tried anyways, but with no error in case of failure.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SkipSecurityCheck {
}