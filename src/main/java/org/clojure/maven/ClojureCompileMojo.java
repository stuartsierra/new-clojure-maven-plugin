package org.clojure.maven;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure source files
 *
 * @requiresDependencyResolution compile
 * @goal compile
 */
public class ClojureCompileMojo extends AbstractClojureCompileMojo {

    /**
     * outputDirectory
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    public void execute() throws MojoExecutionException {
        compile(outputDirectory, Classpath.COMPILE_CLASSPATH | Classpath.COMPILE_SOURCES);
    }
}

