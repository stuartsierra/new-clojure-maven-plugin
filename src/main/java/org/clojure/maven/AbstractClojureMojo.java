package org.clojure.maven;

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
}

