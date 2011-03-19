package org.clojure.maven;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure test source files
 *
 * @requiresDependencyResolution test
 * @goal testCompile
 */
public class ClojureTestCompileMojo extends AbstractClojureCompileMojo {

    /**
     * outputDirectory
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    public void execute() throws MojoExecutionException {
        compile(outputDirectory,
                Classpath.COMPILE_CLASSPATH | Classpath.COMPILE_SOURCES |
                Classpath.TEST_CLASSPATH | Classpath.TEST_SOURCES);
    }
}

