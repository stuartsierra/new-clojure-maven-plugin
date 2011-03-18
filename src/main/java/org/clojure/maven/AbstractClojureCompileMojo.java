package org.clojure.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * The enclosing project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    protected List<String> getClasspathElements() throws Exception {
        return new ArrayList<String>(project.getCompileClasspathElements());
    }

    protected List<URL> getClasspathURLs() throws Exception {
        List<String> paths = getClasspathElements();
        List<URL> urls = new ArrayList<URL>();
        for (int i = 0; i < paths.size(); i++) {
            getLog().debug("Got classpath element: " + paths.get(i));
            urls.add(new File(paths.get(i)).toURL());
        }
        return urls;
    }

    protected ClassLoader getClassLoader() throws Exception {
        return new URLClassLoader((URL[])getClasspathURLs().toArray(new URL[0]));
    }
    
    public void compile() throws MojoExecutionException {
	try {
            IsolatedThreadGroup threadGroup = new IsolatedThreadGroup("clojure-thread-group");
            Thread mainThread = new Thread(threadGroup, new ClojureCompiler(outputDirectory, namespaces));
            mainThread.setContextClassLoader(getClassLoader());
            mainThread.start();
            joinThreads(threadGroup);
	} catch(Exception e) {
	    throw new MojoExecutionException("Clojure compile failed", e);
	}
    }

    private static void joinThreads(ThreadGroup group) throws Exception {
        boolean found;
        do {
            found = false;
            Thread[] threads = new Thread[1];
            int count = group.enumerate(threads);
            if (count > 0) {
                found = true;
                threads[0].join();
            }
        } while (found);
    }

    class ClojureCompiler implements Runnable {
        private String outputDirectory;
        private String[] namespaces;
        
        public ClojureCompiler(String outputDirectory, String[] namespaces) {
            this.outputDirectory = outputDirectory;
            this.namespaces = namespaces;
        }
        
        public void run() {
            PrintStream stdout = System.out;
            try {
                System.setOut(new WrappedPrintStream(System.out));
                System.setProperty("clojure.compile.path", outputDirectory);
                Class compiler = Thread.currentThread().getContextClassLoader().loadClass("clojure.lang.Compile");
                Method mainMethod = compiler.getMethod("main", new Class[] { String[].class });
                Object[] args = new Object[1];
                args[0] = namespaces;
                mainMethod.invoke(null, args);
            } catch (Exception e) {
                Thread.currentThread().getThreadGroup().uncaughtException(Thread.currentThread(), e);
            } finally {
                System.setOut(stdout);
            }
        }
    }

    class IsolatedThreadGroup extends ThreadGroup {
        private Throwable throwable;

        public IsolatedThreadGroup(String name) {
            super(name);
        }

        public void uncaughtException(Thread thread, Throwable throwable) {
            if (throwable instanceof ThreadDeath) {
                // Normal thread completion
                return;
            }
            getLog().error("Exception thrown in IsolatedThreadGroup:", throwable);
        }
    }

    class WrappedPrintStream extends PrintStream {
        public WrappedPrintStream(PrintStream stream) {
            super(stream);
        }
        
        public void close() {
            // Do nothing, to avoid shutting down the output.
        }
    }
}

