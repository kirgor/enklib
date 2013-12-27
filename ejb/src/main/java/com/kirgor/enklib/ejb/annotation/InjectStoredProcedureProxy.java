package com.kirgor.enklib.ejb.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for bean fields, which represent stored procedure proxies. Allows to auto-instantiate
 * stored procedure proxies each time bean method is invoked.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InjectStoredProcedureProxy {
}
