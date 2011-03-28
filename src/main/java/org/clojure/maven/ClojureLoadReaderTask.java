package org.clojure.maven;

import java.io.Reader;

public class ClojureLoadReaderTask implements Runnable {
    private final Reader reader;
        
    public ClojureLoadReaderTask(Reader reader) {
        this.reader = reader;
    }
        
    public void run() {
        ClojureReflector clojure = null;
        try {
            clojure = new ClojureReflector();
            clojure.loadReader(reader);
        } catch (Exception e) {
            Thread.currentThread().getThreadGroup().
                uncaughtException(Thread.currentThread(), e);
        }
    }
}
