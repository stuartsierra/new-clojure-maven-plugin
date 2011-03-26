package org.clojure.maven;

import org.apache.maven.plugin.logging.Log;

public class ClojureEvalTask implements Runnable {
    private static final String PATH_PROP = "clojure.compile.path";
    private static final String REFLECTION_WARNING_PROP = "clojure.compile.warn-on-reflection";
    private static final String UNCHECKED_MATH_PROP = "clojure.compile.unchecked-math";

    private final String eval;
    private final Log log;
        
    public ClojureEvalTask(Log log, String eval) {
        this.eval = eval;
        this.log = log;
    }
        
    public void run() {
        ClojureReflector clojure = null;
        boolean bindingsPushed = false;
        try {
            clojure = new ClojureReflector();
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
            clojure.loadString(eval);
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
