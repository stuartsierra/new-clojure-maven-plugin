package org.clojure.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Compile Clojure source files
 *
 * @requiresDependencyResolution test
 * @goal compile
 */
public class ClojureCompileMojo extends AbstractClojureCompileMojo {

    public void execute() throws MojoExecutionException {
	compile();
    }
}

