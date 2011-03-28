package org.clojure.maven;

public class ClojureEvalTask implements Runnable {
    private final String eval;
        
    public ClojureEvalTask(String eval) {
        this.eval = eval;
    }
        
    public void run() {
        ClojureReflector clojure = null;
        boolean bindingsPushed = false;
        try {
            clojure = new ClojureReflector();
            clojure.loadString(eval);
        } catch (Exception e) {
            Thread.currentThread().getThreadGroup().
                uncaughtException(Thread.currentThread(), e);
        }
    }
}
