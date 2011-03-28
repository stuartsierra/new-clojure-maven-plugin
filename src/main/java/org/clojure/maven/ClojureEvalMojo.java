package org.clojure.maven;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * Base class for running Clojure
 *
 * @goal eval
 */
public class ClojureEvalMojo extends AbstractClojureMojo {

    /**
     * Clojure code to be evaluated
     *
     * @parameter expression="${clojure.eval}"
     * @required
     */
    private String eval;

    public void execute() throws MojoExecutionException {
        if (scope == null) scope = "test";
        run(new ClojureEvalTask(eval));
    }
}

