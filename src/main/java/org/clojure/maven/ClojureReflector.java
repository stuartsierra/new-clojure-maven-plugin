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

    private final Class RTClass;
    private final Class VarClass;

    private final Method mapMethod;
    private final Method varMethod;

    private final Method invoke0;
    private final Method invoke1;
    private final Method invoke2;
    private final Method invoke3;
    private final Method invoke4;
    private final Method invoke5;

    public final Object compileVar;
    public final Object compilePathVar;
    public final Object pushThreadBindingsVar;
    public final Object popThreadBindingsVar;
    public final Object symbolVar;
    public final Object uncheckedMathVar;
    public final Object warnOnReflectionVar;

    public ClojureReflector() throws Exception {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
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
    }

    /** Returns the Clojure Var with the given namespace and name. */
    public Object var(String namespace, String name) throws Exception {
        return varMethod.invoke(null, namespace, name);
    }

    /** Returns a Clojure IPersistentMap given a series of key-value
     * pairs. */
    public Object map(Object... args) throws Exception {
        return mapMethod.invoke(null, (Object)args);
    }

    /** Invokes a Var as a function with no arguments */
    public Object invoke(Object theVar) throws Exception{
        return invoke0.invoke(theVar);
    }

    public Object invoke(Object theVar, Object arg1) throws Exception{
        return invoke1.invoke(theVar, arg1);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2) throws Exception{
        return invoke2.invoke(theVar, arg1, arg2);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2, Object arg3) throws Exception{
        return invoke3.invoke(theVar, arg1, arg2, arg3);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2, Object arg3, Object arg4) throws Exception{
        return invoke4.invoke(theVar, arg1, arg2, arg3, arg4);
    }

    public Object invoke(Object theVar, Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) throws Exception{
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
