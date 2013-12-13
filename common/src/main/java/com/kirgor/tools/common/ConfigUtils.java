package com.kirgor.tools.common;

import com.kirgor.tools.common.annotation.ConfigPrefix;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Contains methods related to application configuration.
 */
public abstract class ConfigUtils {
    /**
     * Loads config values from system properties, which may be set by application server or using -Dkey=value.
     * Values are stored in static fields of specified class.
     * <p/>
     * System property names are expected to be in lower camel case like "smtpHost", while class static fields
     * are expected to be in upper snake case (by Java naming convension) like "SMTP_HOST".
     * <p/>
     * Config class may be annotated with {@link ConfigPrefix}. In that case, system property names will be
     * expected with some specified prefix delimeterd by dot ".".
     * For example, you've specified @ConfigPrefix("app1"), then property names should be like "app1.host", "app1.port" etc.
     * It's useful for enterprise application, where multiple apps have to share the same list of system properties and
     * they are likely to be prefixed.
     *
     * @param configClass Class, which static fields are used to store config values.
     * @throws IllegalAccessException
     */
    public static void loadFromSystemProperties(Class configClass) throws IllegalAccessException {
        Annotation annotation = configClass.getAnnotation(ConfigPrefix.class);
        String prefix = annotation != null ? ((ConfigPrefix) annotation).value() + "." : "";

        Field[] fields = configClass.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                String name = NamingUtils.snakeToLowerCamel(field.getName());
                String propertyName = prefix + name;

                String stringValue = System.getProperty(propertyName);
                if (stringValue != null) {
                    Object value = null;
                    if (field.getType() == String.class) {
                        value = stringValue;
                    } else if (field.getType() == int.class || field.getType() == Integer.class) {
                        value = Integer.parseInt(stringValue);
                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        value = Boolean.parseBoolean(stringValue);
                    }
                    field.set(null, value);
                }
            }
        }
    }

    public static <T> T loadFromXMLFile(Class<T> configClass, File file) throws Exception {
        Persister persister = new Persister();
        return persister.read(configClass, file);
    }

    public static <T> T loadFromXMLStream(Class<T> configClass, InputStream inputStream) throws Exception {
        Persister persister = new Persister();
        return persister.read(configClass, inputStream);
    }
}
