package com.kirgor.enklib.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class MemoryJavaClassObject extends SimpleJavaFileObject {
    private String className;
    protected final ByteArrayOutputStream stream = new ByteArrayOutputStream();

    /**
     * Creates instance of {@link MemoryJavaClassObject}.
     *
     * @param className Name of class to store.
     * @param kind      Kind of the data.
     */
    public MemoryJavaClassObject(String className, Kind kind) {
        super(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind);
        this.className = className;
    }

    /**
     * Gets compiled byte code.
     */
    public byte[] getBytes() {
        return stream.toByteArray();
    }

    /**
     * Gets the name of stored class.
     */
    public String getClassName() {
        return className;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OutputStream openOutputStream() throws IOException {
        return stream;
    }
}
