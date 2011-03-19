package org.clojure.maven;

import java.lang.reflect.Method;

public class ClojureCompileTask implements Runnable {
    private final String outputDirectory;
    private final String[] namespaces;
        
    public ClojureCompileTask(String outputDirectory, String[] namespaces) {
        this.outputDirectory = outputDirectory;
        this.namespaces = namespaces;
    }
        
    public void run() {
        try {
            System.setProperty("clojure.compile.path", outputDirectory);
            Class compiler = Thread.currentThread().getContextClassLoader().loadClass("clojure.lang.Compile");
            Method mainMethod = compiler.getMethod("main", new Class[] { String[].class });
            Object[] args = new Object[1];
            args[0] = namespaces;
            mainMethod.invoke(null, args);
        } catch (Exception e) {
            Thread.currentThread().getThreadGroup().uncaughtException(Thread.currentThread(), e);
        }
    }
}
