package org.clojure.maven;

import java.lang.reflect.Method;

/** Utility class for accessing the Clojure runtime via the Java
 * reflection API, to avoid any compile-time dependencies on a
 * particular Clojure version.
 *
 * This class is not static because it may be loaded multiple times
 * with different classloaders. */
public class ClojureReflector {
    private static final String COMPILE_PATH_PROP = "clojure.compile.path";
    private static final String WARN_ON_REFLECTION_PROP = "clojure.compile.warn-on-reflection";
    private static final String UNCHECKED_MATH_PROP = "clojure.compile.unchecked-math";

    private Class RTClass;
    private Class VarClass;

    private Method mapMethod;
    private Method varMethod;

    private Method invoke0;
    private Method invoke1;
    private Method invoke2;
    private Method invoke3;
    private Method invoke4;
    private Method invoke5;

    public Object compileVar;
    public Object compilePathVar;
    public Object pushThreadBindingsVar;
    public Object popThreadBindingsVar;
    public Object symbolVar;
    public Object uncheckedMathVar;
    public Object warnOnReflectionVar;

    public ClojureReflector() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            RTClass = classloader.loadClass("clojure.lang.RT");
            VarClass = classloader.loadClass("clojure.lang.Var");

            mapMethod = RTClass.getMethod("map", new Class[]{Object[].class});
            varMethod = RTClass.getMethod("var", new Class[]{String.class, String.class});

            invoke0 = VarClass.getMethod("invoke", new Class[]{});
            invoke1 = VarClass.getMethod("invoke", new Class[]{Object.class});
            invoke2 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class});
            invoke3 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class, Object.class});
            invoke4 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class, Object.class, Object.class});
            invoke5 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class, Object.class, Object.class, Object.class});

            compileVar = var("clojure.core", "compile");
            compilePathVar = var("clojure.core", "*compile-path*");
            pushThreadBindingsVar = var("clojure.core", "push-thread-bindings");
            popThreadBindingsVar = var("clojure.core", "pop-thread-bindings");
            symbolVar = var("clojure.core", "symbol");
            uncheckedMathVar = var("clojure.core", "*unchecked-math*");
            warnOnReflectionVar = var("clojure.core", "*warn-on-reflection*");
        } catch (Exception e) {
            System.err.println("Failed to load Clojure");
            e.printStackTrace();
        }
    }

    /** Returns the Clojure Var with the given namespace and name. */
    public Object var(String namespace, String name) throws Exception {
        if (varMethod == null)
            throw new IllegalStateException("Clojure failed to load");
        return varMethod.invoke(null, namespace, name);
    }

    /** Returns a Clojure IPersistentMap given a series of key-value
     * pairs. */
    public Object map(Object... args) throws Exception {
        if (mapMethod == null)
            throw new IllegalStateException("Clojure failed to load");
        return mapMethod.invoke(null, (Object)args);
    }

    /** Invokes a Var as a function with no arguments */
    public Object invoke(Object theVar) throws Exception{
        if (invoke0 == null)
            throw new IllegalStateException("Clojure failed to load");
        return invoke0.invoke(theVar);
    }

    public Object invoke(Object theVar, Object arg1) throws Exception{
        if (invoke1 == null)
            throw new IllegalStateException("Clojure failed to load");
        return invoke1.invoke(theVar, arg1);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2) throws Exception{
        if (invoke2 == null)
            throw new IllegalStateException("Clojure failed to load");
        return invoke2.invoke(theVar, arg1, arg2);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2, Object arg3) throws Exception{
        if (invoke3 == null)
            throw new IllegalStateException("Clojure failed to load");
        return invoke3.invoke(theVar, arg1, arg2, arg3);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2, Object arg3, Object arg4) throws Exception{
        if (invoke4 == null)
            throw new IllegalStateException("Clojure failed to load");
        return invoke4.invoke(theVar, arg1, arg2, arg3, arg4);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) throws Exception{
        if (invoke5 == null)
            throw new IllegalStateException("Clojure failed to load");
        return invoke5.invoke(theVar, arg1, arg2, arg3, arg4, arg5);
    }

    public void pushThreadBindings(Object bindings) throws Exception {
        invoke(pushThreadBindingsVar, bindings);
    }

    public void popThreadBindings() throws Exception {
        invoke(popThreadBindingsVar);
    }

    public Object symbol(String name) throws Exception {
        return invoke(symbolVar, name);
    }

    public Object compile(Object symbol) throws Exception {
        return invoke(compileVar, symbol);
    }
}
