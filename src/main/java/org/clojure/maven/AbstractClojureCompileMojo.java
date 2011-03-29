package org.clojure.maven;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure source files
 */
public abstract class AbstractClojureCompileMojo extends AbstractClojureMojo {

    /**
     * outputDirectory
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    protected String outputDirectory;

    /**
     * @parameter
     * @required
     */
    protected String[] namespaces;

    public void execute() throws MojoExecutionException {
        System.setProperty("clojure.compile.path", outputDirectory);
        System.setProperty("org.clojure.maven.namespaces", Util.join(namespaces));
        System.out.println("Classpath scope: " + scope);
        InputStream stream = this.getClass().getClassLoader().
            getResourceAsStream("org/clojure/maven/compile.clj");
        run(new ClojureLoadReaderTask(new InputStreamReader(stream)));
    }
}


