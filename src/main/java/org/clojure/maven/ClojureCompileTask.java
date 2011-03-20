package org.clojure.maven;

import org.clojure.maven.reflect.RT;
import org.clojure.maven.reflect.Var;
import org.clojure.maven.reflect.Symbol;

public class ClojureCompileTask implements Runnable {
    private static final String PATH_PROP = "clojure.compile.path";
    private static final String REFLECTION_WARNING_PROP = "clojure.compile.warn-on-reflection";
    private static final String UNCHECKED_MATH_PROP = "clojure.compile.unchecked-math";

    private final String outputDirectory;
    private final String[] namespaces;
        
    public ClojureCompileTask(String outputDirectory, String[] namespaces) {
        this.outputDirectory = outputDirectory;
        this.namespaces = namespaces;
    }
        
    public void run() {
        try {
            Var compile = RT.var("clojure.core", "compile");
            Var compile_path = RT.var("clojure.core", "*compile-path*");
            Var with_bindings = RT.var("clojure.core", "with-bindings*");

            Object bindings = RT.map(compile_path.var, System.getProperty("clojure.compile.path"));
            with_bindings.invoke(bindings, compile.var, Symbol.intern(namespaces[0]));
        } catch (Exception e) {
            Thread.currentThread().getThreadGroup().uncaughtException(Thread.currentThread(), e);
        }
    }
}
