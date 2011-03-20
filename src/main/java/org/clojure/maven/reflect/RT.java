package org.clojure.maven.reflect;

import java.lang.reflect.Method;

public class RT {
    private static Class RTClass;
    private static Method varMethod;
    private static Method mapMethod;

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            RTClass = classloader.loadClass("clojure.lang.RT");
            varMethod = RTClass.getMethod("var", new Class[]{String.class, String.class});
            mapMethod = RTClass.getMethod("map", new Class[]{Object[].class});
        } catch (Exception e) {
            System.err.println("Failed to load clojure.lang.RT");
            e.printStackTrace();
        }
    }

    public static Var var(String namespace, String name) throws Exception {
        if (varMethod == null)
            throw new IllegalStateException("clojure.lang.RT was not loaded");
        return new Var(varMethod.invoke(null, namespace, name));
    }

    public static Object map(Object... init) throws Exception {
        if (mapMethod == null)
            throw new IllegalStateException("clojure.lang.RT was not loaded");
        return mapMethod.invoke(null, (Object)init);
    }
}