package org.clojure.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.lang.reflect.Method;

/**
 * Base class for running the Clojure compiler.
 *
 * @requiresDependencyResolution test
 */
public abstract class AbstractClojureCompileMojo extends AbstractMojo {

    /**
     * Namespaces to be compiled.
     * @parameter
     * @required
     */
    private String[] namespaces;

    /**
     * outputDirectory
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    public void compile() throws MojoExecutionException {
	try {
	    System.setProperty("clojure.compile.path", outputDirectory);
	    Class compiler = Class.forName("clojure.lang.Compile");
	    Method mainMethod = compiler.getMethod("main", new Class[] { String[].class });
	    mainMethod.invoke(null, namespaces);
	} catch(Exception e) {
	    throw new MojoExecutionException("Clojure compile failed", e);
	}
    }
}

