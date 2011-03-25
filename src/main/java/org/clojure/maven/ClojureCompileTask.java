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
        ClojureReflector clojure = new ClojureReflector();
        boolean bindingsPushed = false;
        try {
            Object bindings =
                clojure.
                map(clojure.compilePathVar,
                    System.getProperty(PATH_PROP),
                    clojure.warnOnReflectionVar,
                    "true".equals(System.getProperty(REFLECTION_WARNING_PROP)),
                    clojure.uncheckedMathVar,
                    "true".equals(System.getProperty(UNCHECKED_MATH_PROP)));
            clojure.pushThreadBindings(bindings);
            bindingsPushed = true;
            for (int i = 0; i < namespaces.length; i++) {
                log.info("Compiling " + namespaces[i]);
                clojure.compile(clojure.symbol(namespaces[i]));
            }
        } catch (Exception e) {
            Thread.currentThread().getThreadGroup().
                uncaughtException(Thread.currentThread(), e);
        } finally {
            if (bindingsPushed) {
                try {
                    clojure.popThreadBindings();
                } catch (Exception e) {
                    Thread.currentThread().getThreadGroup().
                        uncaughtException(Thread.currentThread(), e);
                }
            }
        }
    }
}
