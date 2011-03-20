package org.clojure.maven.reflect;

import java.lang.reflect.Method;

public class Var {
    private static Class VarClass;
    private static Method invoke0;
    private static Method invoke1;
    private static Method invoke2;
    private static Method invoke3;
    private static Method invoke4;
    private static Method invoke5;

    public final Object var;

    static {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
            VarClass = classloader.loadClass("clojure.lang.Var");
            invoke0 = VarClass.getMethod("invoke", new Class[]{});
            invoke1 = VarClass.getMethod("invoke", new Class[]{Object.class});
            invoke2 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class});
            invoke3 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class, Object.class});
            invoke4 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class, Object.class, Object.class});
            invoke5 = VarClass.getMethod("invoke", new Class[]{Object.class, Object.class, Object.class, Object.class, Object.class});
        } catch (Exception e) {
            System.err.println("Failed to load clojure.lang.Var");
            e.printStackTrace();
        }
    }

    Var(Object var) {
        this.var = var;
    }

    public Object invoke() throws Exception{
        if (invoke0 == null)
            throw new IllegalStateException("clojure.lang.Var was not loaded");
        return invoke0.invoke(var);
    }

    public Object invoke(Object arg1) throws Exception{
        if (invoke1 == null)
            throw new IllegalStateException("clojure.lang.Var was not loaded");
        return invoke1.invoke(var, arg1);
    }

    public Object invoke(Object arg1, Object arg2) throws Exception{
        if (invoke2 == null)
            throw new IllegalStateException("clojure.lang.Var was not loaded");
        return invoke2.invoke(var, arg1, arg2);
    }

    public Object invoke(Object arg1, Object arg2, Object arg3) throws Exception{
        if (invoke3 == null)
            throw new IllegalStateException("clojure.lang.Var was not loaded");
        return invoke3.invoke(var, arg1, arg2, arg3);
    }

    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4) throws Exception{
        if (invoke4 == null)
            throw new IllegalStateException("clojure.lang.Var was not loaded");
        return invoke4.invoke(var, arg1, arg2, arg3, arg4);
    }

    public Object invoke(Object arg1, Object arg2, Object arg3, Object arg4, Object arg5) throws Exception{
        if (invoke5 == null)
            throw new IllegalStateException("clojure.lang.Var was not loaded");
        return invoke4.invoke(var, arg1, arg2, arg3, arg4, arg5);
    }
}
