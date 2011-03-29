package org.clojure.maven;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure test sources
 *
 * @requiresDependencyResolution test
 * @goal testCompile
 */
public class ClojureTestCompileMojo extends AbstractClojureCompileMojo {

    public void execute() throws MojoExecutionException {
        if (scope == null) scope = "test";
        super.execute();
    }
}
