package org.clojure.maven;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure source or test files
 *
 */
public abstract class AbstractClojureCompileMojo extends AbstractClojureMojo {

    /**
     * Namespaces to be compiled.
     * @parameter
     * @required
     */
    protected String[] namespaces;

    public void compile(String outputDirectory, int classpathScope)
        throws MojoExecutionException {

        try {
            new File(outputDirectory).mkdirs();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to create output directory", e);
        }
        System.setProperty("clojure.compile.path", outputDirectory);
        Classpath classpath;
        try {
            classpath = new Classpath(project, classpathScope, null);
        } catch (Exception e) {
            throw new MojoExecutionException("Classpath initialization failed", e);
        }
        IsolatedThreadRunner runner =
            new IsolatedThreadRunner(getLog(), classpath,
                                     new ClojureCompileTask(getLog(), namespaces));
        runner.run();
        Throwable t = runner.getUncaught();
        if (t != null) {
            throw new MojoExecutionException("Clojure compilation failed", t);
        }
    }
}

