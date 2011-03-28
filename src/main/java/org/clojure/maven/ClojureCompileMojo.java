package org.clojure.maven;

import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Ahead-of-time (AOT) compile Clojure source files
 *
 * @requiresDependencyResolution compile
 * @goal compile
 */
public class ClojureCompileMojo extends AbstractClojureMojo {

    /**
     * outputDirectory
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     * @readonly
     */
    private String outputDirectory;

    /**
     * @parameter
     * @required
     */
    private String[] namespaces;

    public void execute() throws MojoExecutionException {
        if (scope == null) scope = "compile";
        System.setProperty("clojure.compile-path", outputDirectory);
        System.setProperty("org.clojure.maven.namespaces", Util.join(namespaces));
        System.out.println("Classpath scope: " + scope);
        InputStream stream = this.getClass().getClassLoader().
            getResourceAsStream("org/clojure/maven/compile.clj");
        run(new ClojureLoadReaderTask(new InputStreamReader(stream)));
    }
}


