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
            System.setProperty("clojure.compile.path", outputDirectory);
            Classpath classpath = new Classpath(project, classpathScope, null);
            URLClassLoader classloader = classpath.getClassLoader();
            getLog().debug("Classpath URLs: " + Arrays.toString(classloader.getURLs()));
            runIsolated(classloader, new ClojureCompileTask(outputDirectory, namespaces));
        } catch (Exception e) {
            throw new MojoExecutionException("Clojure execution failed", e);
        }
    }
}

