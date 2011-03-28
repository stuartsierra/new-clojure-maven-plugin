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
 */
public abstract class AbstractClojureMojo extends AbstractMojo {

    /**
     * The enclosing project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Classpath scope: one of 'compile', 'test', or
     * 'runtime'. Defaults to 'test'.
     *
     * @parameter expression="${clojure.scope}"
     */
    protected String scope;

    /**
     * If true (default), include project source directories on the classpath.
     *
     * @parameter expression="${clojure.includeSources}" default-value="true"
     * @required
     */
    protected boolean includeSources;

    /**
     * If true, include project resource directories on the classpath.
     * Defaults to false, as resources will normally be copied into
     * the compile path during the copy-resources phase.
     *
     * @parameter expression="${clojure.includeResources}" default-value="false"
     * @required
     */
    protected boolean includeResources;

    public void run(Runnable task) throws MojoExecutionException {
        Classpath classpath;
        try {
            classpath = Classpath.forScope(project, scope, includeSources, includeResources,
                                           null);
        } catch (Exception e) {
            throw new MojoExecutionException("Classpath initialization failed", e);
        }
        IsolatedThreadRunner runner =
            new IsolatedThreadRunner(getLog(), classpath, task);
        runner.run();
        Throwable t = runner.getUncaught();
        if (t != null) {
            throw new MojoExecutionException("Clojure evaluation failed", t);
        }
    }
}

