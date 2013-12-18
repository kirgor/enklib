package com.kirgor.enklib.compile;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * {@link javax.tools.JavaFileObject}, which stores source code in memory.
 */
public class MemoryJavaFileObject extends SimpleJavaFileObject {
    private String content;

    /**
     * Creates instance of {@link MemoryJavaFileObject}.
     *
     * @param className Name of class to store.
     * @param code      Source code.
     */
    public MemoryJavaFileObject(String className, String code) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = code;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return content;
    }
}