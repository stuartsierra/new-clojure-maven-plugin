package org.clojure.maven;

import java.lang.reflect.Method;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Compile Clojure source files
 *
 * @requiresDependencyResolution test
 * @goal compile
 */
public class ClojureCompileMojo extends AbstractClojureMojo {

    /**
     * Namespaces to be compiled.
     * @parameter
     * @required
     */
    private String[] namespaces;

    /**
     * outputDirectory
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    public void execute() throws MojoExecutionException {
        try {
            Classpath classpath = new Classpath(project, Classpath.COMPILE_CLASSPATH | Classpath.COMPILE_SOURCES, null);
            getLog().debug("Classpath: " + classpath);
            runIsolated(classpath, new ClojureCompiler(outputDirectory, namespaces));
        } catch (Exception e) {
            throw new MojoExecutionException("Clojure execution failed", e);
        }
    }

    class ClojureCompiler implements Runnable {
        private String outputDirectory;
        private String[] namespaces;
        
        public ClojureCompiler(String outputDirectory, String[] namespaces) {
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
}

