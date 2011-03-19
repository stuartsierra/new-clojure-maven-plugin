package org.clojure.maven;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Compile Clojure test source files
 *
 * @requiresDependencyResolution test
 * @goal test-compile
 */
public class ClojureTestCompileMojo extends AbstractClojureMojo {

    /**
     * Namespaces to be compiled.
     * @parameter
     * @required
     */
    private String[] namespaces;

    /**
     * outputDirectory
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    public void execute() throws MojoExecutionException {
        try {
            new File(outputDirectory).mkdirs();
            Classpath classpath = new Classpath(project, Classpath.COMPILE_CLASSPATH | Classpath.COMPILE_SOURCES | Classpath.TEST_CLASSPATH | Classpath.TEST_SOURCES, null);
            URLClassLoader classloader = classpath.getClassLoader();
            getLog().debug("Classpath URLs: " + Arrays.toString(classloader.getURLs()));
            runIsolated(classloader, new ClojureCompileTask(outputDirectory, namespaces));
        } catch (Exception e) {
            throw new MojoExecutionException("Clojure execution failed", e);
        }
    }
}

