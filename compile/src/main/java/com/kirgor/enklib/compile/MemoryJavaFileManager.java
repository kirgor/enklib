package com.kirgor.enklib.compile;

import javax.tools.*;
import java.io.IOException;
import java.security.SecureClassLoader;

/**
 * Manages the final stage of compiler pipeline and directs results into memory object.
 */
public class MemoryJavaFileManager extends ForwardingJavaFileManager {
    private MemoryJavaClassObject javaClassObject;

    /**
     * Creates instance of {@link MemoryJavaFileManager}.
     *
     * @param standardManager Standard manager, which will be used as base manager.
     */
    public MemoryJavaFileManager(StandardJavaFileManager standardManager) {
        super(standardManager);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ClassLoader getClassLoader(Location location) {
        return new MemoryClassLoader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        javaClassObject = new MemoryJavaClassObject(className, kind);
        return javaClassObject;
    }

    class MemoryClassLoader extends SecureClassLoader {
        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            if (name.equals(javaClassObject.getClassName())) {
                byte[] bytes = javaClassObject.getBytes();
                return super.defineClass(name, bytes, 0, bytes.length);
            } else {
                return Class.forName(name);
            }
        }
    }
}
