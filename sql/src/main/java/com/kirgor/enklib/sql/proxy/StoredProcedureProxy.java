package com.kirgor.enklib.sql.proxy;

import com.kirgor.enklib.compile.CompileUtils;
import com.kirgor.enklib.sql.Cursor;
import com.kirgor.enklib.sql.EntityUtils;
import com.kirgor.enklib.sql.Session;
import com.kirgor.enklib.sql.dialect.Dialect;
import com.kirgor.enklib.sql.proxy.annotation.*;

import javax.tools.SimpleJavaFileObject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.*;

public abstract class StoredProcedureProxy {
    protected Session session;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public static Class createProxyClass(Class interfaceClass, Dialect dialect) throws Exception {
        List<Class> classPath = new ArrayList<Class>();
        classPath.add(interfaceClass);
        classPath.add(StoredProcedureProxy.class);
        classPath.add(Dialect.class);
        classPath.add(Cursor.class);
        classPath.add(Session.class);
        classPath.add(ParameterUtils.class);
        classPath.add(LowerCase.class);
        classPath.add(UpperCase.class);
        classPath.add(LikePrefix.class);
        classPath.add(BitMask.class);
        classPath.add(Timestamp.class);

        String name = "Proxy" + UUID.randomUUID().toString().replace("-", "");
        String code = buildProxyCode(name, interfaceClass, dialect, classPath);
        return CompileUtils.compileClass("com.kirgor.enklib.sql.proxy." + name, code, classPath);
    }

    private static String buildProxyCode(String className, Class interfaceClass, Dialect dialect, List<Class> classPath) {
        StringBuilder result = new StringBuilder();
        HashSet<Class> entityClasses = new HashSet<Class>();

        result.append("package com.kirgor.enklib.sql.proxy;");

        // Class header
        result.append("public class ").append(className)
                .append(" extends com.kirgor.enklib.sql.proxy.StoredProcedureProxy implements ")
                .append(interfaceClass.getCanonicalName()).append("{");

        // Go through all methods found in interface
        for (Method method : interfaceClass.getDeclaredMethods()) {
            Class<?> returnType = method.getReturnType();
            Type genericReturnType = method.getGenericReturnType();
            ParameterizedType parameterizedReturnType = null;
            if (genericReturnType instanceof ParameterizedType) {
                parameterizedReturnType = (ParameterizedType) genericReturnType;
            }

            // Method header start (until opening parenthesis)
            result.append("@Override public ")
                    .append(returnType.getCanonicalName())
                    .append(" ")
                    .append(method.getName())
                    .append("(");

            // Method formal parameters ("p0,p1,p2")
            Class<?>[] parameterClasses = method.getParameterTypes();
            for (int i = 0; i < parameterClasses.length; i++) {
                if (i > 0) {
                    result.append(",");
                }
                result.append(parameterClasses[i].getCanonicalName()).append(" p").append(i);
            }

            // Method header end and body start
            result.append(")throws java.lang.Exception{");

            // Declare intermediate variables for all parameters. It's useful for pre-processed params
            final String utilsClass = "com.kirgor.enklib.sql.proxy.ParameterUtils.";
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for (int i = 0; i < parameterAnnotations.length; i++) {
                // Wrap parameter into pre-processor methods
                String processedParam = "p" + i;
                for (Annotation annotation : parameterAnnotations[i]) {
                    if (annotation instanceof LowerCase) {
                        processedParam = utilsClass + "lowerCase(" + processedParam + ")";
                    } else if (annotation instanceof UpperCase) {
                        processedParam = utilsClass + "upperCase(" + processedParam + ")";
                    } else if (annotation instanceof LikePrefix) {
                        processedParam = utilsClass + "likePrefix(" + processedParam + ")";
                    } else if (annotation instanceof Timestamp) {
                        processedParam = utilsClass + "timestamp(" + processedParam + ")";
                    } else if (annotation instanceof BitMask) {
                        processedParam = utilsClass + "bitMask(" + processedParam + ")";
                    }
                }

                // Declare an intermediate variable
                result.append("Object pp").append(i).append("=").append(processedParam).append(";");
            }

            // Generate SQL query for calling stored procedure. Also, consider escaping brackets,
            // since this query will end up being in source code
            String escapedQuery = dialect.buildStoredProcedureQuery(dialect.toDatabaseName(method.getName()),
                    parameterClasses.length).replace("\"", "\\\"");

            // The rest of method body depends on its return type
            String callStart;
            Class entityClass;
            if (returnType == Void.TYPE) {
                callStart = "session.execute";
                entityClass = null;
            } else if (returnType == List.class) {
                callStart = "return session.getList";
                entityClass = (Class) parameterizedReturnType.getActualTypeArguments()[0];
            } else if (returnType == Cursor.class) {
                callStart = "return session.getCursor";
                entityClass = (Class) parameterizedReturnType.getActualTypeArguments()[0];
            } else {
                callStart = "return session.getSingleOrNull";
                entityClass = returnType;
            }

            // Add entity class to classpath only if it's not primitive
            if (entityClass != null && !EntityUtils.isPrimitive(entityClass)) {
                Stack<Class> stack = new Stack<Class>();
                stack.push(entityClass);
                while (!stack.isEmpty()) {
                    Class c = stack.pop();

                    if (c.getEnclosingClass() != null) {
                        stack.push(c.getEnclosingClass());
                    }

                    if (c.getSuperclass() != null) {
                        stack.push(c.getSuperclass());
                    }

                    entityClasses.add(c);
                }
            }

            result.append(callStart)
                    .append("(\"")
                    .append(escapedQuery)
                    .append("\"");

            if (entityClass != null) {
                result.append(",").append(entityClass.getCanonicalName()).append(".class");
            }

            // Append actual parameters
            for (int i = 0; i < parameterClasses.length; i++) {
                result.append(",pp").append(i);
            }
            result.append(");");

            // Method body end
            result.append("}");
        }

        // Class declaration end
        result.append("}");

        // Add entity classes to the classpath
        for (Class entityClass : entityClasses) {
            classPath.add(entityClass);
        }

        return result.toString();
    }
}
