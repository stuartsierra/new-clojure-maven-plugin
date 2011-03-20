package org.clojure.maven.reflect;

import java.lang.reflect.Method;

public class Symbol {
    private static Class SymbolClass;
    private static Method internMethod;

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            SymbolClass = classloader.loadClass("clojure.lang.Symbol");
            internMethod = SymbolClass.getMethod("intern", new Class[]{String.class});
        } catch (Exception e) {
            System.err.println("Failed to load clojure.lang.Symbol");
            e.printStackTrace();
        }
    }

    public static Object intern(String name) throws Exception {
        if (internMethod == null)
            throw new IllegalStateException("clojure.lang.Symbol was not loaded");
        return internMethod.invoke(null, name);
    }
}