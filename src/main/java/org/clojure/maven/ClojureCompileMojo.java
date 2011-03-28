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
        System.setProperty("clojure.compile-path", outputDirectory);
        System.setProperty("org.clojure.maven.namespaces", join(namespaces));
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("org/clojure/maven/compile.clj");
        run(new ClojureLoadReaderTask(new InputStreamReader(stream)));
    }

    private static String join(String[] args) {
        StringBuilder s = new StringBuilder();
        s.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            s.append(",");
            s.append(args[i]);
        }
        return s.toString();
    }
}


