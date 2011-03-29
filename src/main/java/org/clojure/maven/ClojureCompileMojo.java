package org.clojure.maven;

import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure source files
 *
 * @requiresDependencyResolution compile
 * @goal compile
 */
public class ClojureCompileMojo extends AbstractClojureCompileMojo {

    public void execute() throws MojoExecutionException {
        if (scope == null) scope = "compile";
        super.execute();
    }
}
