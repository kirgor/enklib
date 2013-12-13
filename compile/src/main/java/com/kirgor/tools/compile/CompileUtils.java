package com.kirgor.tools.compile;

import org.apache.commons.io.IOUtils;

import javax.tools.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains methods for compiling Java code.
 */
public class CompileUtils {
    /**
     * Compiles Java code and returns compiled class back.
     */
    public static Class compileClass(String name, String code, List<Class> classPath) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        MemoryJavaFileManager fileManager = new MemoryJavaFileManager(compiler.getStandardFileManager(null, null, null));

        List<JavaFileObject> javaFileObjects = new ArrayList<JavaFileObject>();
        javaFileObjects.add(new MemoryJavaFileObject(name, code));

        File tempDir = new File(System.getProperty("java.io.tmpdir"), name);
        List<String> options = new ArrayList<String>();
        if (classPath.size() > 0) {
            options.add("-classpath");
            options.add(tempDir.getAbsolutePath());

            for (Class c : classPath) {
                String classRelativePath = c.getName().replace('.', '/') + ".class";
                URL classInputLocation = c.getResource('/' + classRelativePath);

                File outputFile = new File(tempDir, classRelativePath);
                outputFile.getParentFile().mkdirs();
                InputStream input = classInputLocation.openStream();
                FileOutputStream output = new FileOutputStream(outputFile);
                IOUtils.copy(input, output);
                input.close();
                output.close();
            }
        }

        Boolean success = compiler.getTask(null, fileManager, diagnostics, options, null, javaFileObjects).call();
        if (success) {
            return fileManager.getClassLoader(null).loadClass(name);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                stringBuilder.append(diagnostic.getMessage(null)).append("\n");
            }
            throw new Exception(stringBuilder.toString());
        }
    }
}
