package org.clojure.maven;

import org.apache.maven.plugin.logging.Log;

public class ClojureCompileTask implements Runnable {
    private static final String PATH_PROP = "clojure.compile.path";
    private static final String REFLECTION_WARNING_PROP = "clojure.compile.warn-on-reflection";
    private static final String UNCHECKED_MATH_PROP = "clojure.compile.unchecked-math";

    private final String outputDirectory;
    private final String[] namespaces;
    private final Log log;
        
    public ClojureCompileTask(Log log, String outputDirectory, String[] namespaces) {
        this.outputDirectory = outputDirectory;
        this.namespaces = namespaces;
        this.log = log;
    }
        
    public void run() {
        boolean bindingsPushed = false;
        try {
            Object bindings =
                ClojureReflector.
                map(ClojureReflector.compilePathVar,
                    System.getProperty(PATH_PROP),
                    ClojureReflector.warnOnReflectionVar,
                    "true".equals(System.getProperty(REFLECTION_WARNING_PROP)),
                    ClojureReflector.uncheckedMathVar,
                    "true".equals(System.getProperty(UNCHECKED_MATH_PROP)));
            ClojureReflector.pushThreadBindings(bindings);
            bindingsPushed = true;
            for (int i = 0; i < namespaces.length; i++) {
                log.info("Compiling " + namespaces[i]);
                ClojureReflector.compile(ClojureReflector.symbol(namespaces[i]));
            }
        } catch (Exception e) {
            Thread.currentThread().getThreadGroup().
                uncaughtException(Thread.currentThread(), e);
        } finally {
            if (bindingsPushed) {
                try {
                    ClojureReflector.popThreadBindings();
                } catch (Exception e) {
                    Thread.currentThread().getThreadGroup().
                        uncaughtException(Thread.currentThread(), e);
                }
            }
        }
    }
}
