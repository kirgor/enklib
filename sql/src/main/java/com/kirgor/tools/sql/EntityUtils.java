package com.kirgor.tools.sql;

import java.util.Date;
import java.util.HashSet;

/**
 * Contains methods related to entity classes.
 */
public class EntityUtils {
    private static final HashSet<Class> PRIMITIVE_CLASSES = new HashSet<Class>();

    static {
        PRIMITIVE_CLASSES.add(Byte.class);
        PRIMITIVE_CLASSES.add(Short.class);
        PRIMITIVE_CLASSES.add(Integer.class);
        PRIMITIVE_CLASSES.add(Long.class);
        PRIMITIVE_CLASSES.add(Character.class);
        PRIMITIVE_CLASSES.add(String.class);
        PRIMITIVE_CLASSES.add(Boolean.class);
        PRIMITIVE_CLASSES.add(Float.class);
        PRIMITIVE_CLASSES.add(Double.class);
        PRIMITIVE_CLASSES.add(Date.class);
    }

    /**
     * Determines if the specified class is primitive.
     */
    public static boolean isPrimitive(Class entityClass) {
        return PRIMITIVE_CLASSES.contains(entityClass);
    }
}
