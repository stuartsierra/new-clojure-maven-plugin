package org.clojure.maven;

import java.io.File;
import java.net.URLClassLoader;
import java.util.Arrays;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Evaluate Clojure source code
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

    /**
     * Classpath scope: one of 'compile', 'test', or
     * 'runtime'. Defaults to 'test'.
     *
     * @parameter expression="${clojure.scope}" default-value="test"
     * @required
     */
    private String scope;

    /**
     * If true (default), include project source directories on the classpath.
     *
     * @parameter expression="${clojure.includeSources}" default=value="true"
     * @required
     */
    private boolean includeSources;

    /**
     * If true, include project resource directories on the classpath.
     * Defaults to false, as resources will normally be copied into
     * the compile path during the copy-resources phase.
     *
     * @parameter expression="${clojure.includeResources}" default-value="false"
     * @required
     */
    private boolean includeResources;
    
    public void execute() throws MojoExecutionException {
        Classpath classpath;
        try {
            classpath = Classpath.forScope(project, scope, includeSources, includeResources,
                                           null);
        } catch (Exception e) {
            throw new MojoExecutionException("Classpath initialization failed", e);
        }
        IsolatedThreadRunner runner =
            new IsolatedThreadRunner(getLog(), classpath,
                                     new ClojureEvalTask(getLog(), eval));
        runner.run();
        Throwable t = runner.getUncaught();
        if (t != null) {
            throw new MojoExecutionException("Clojure evaluation failed", t);
        }
    }
}

