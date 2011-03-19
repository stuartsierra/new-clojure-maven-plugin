package org.clojure.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

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

    public void runIsolated(ClassLoader classloader, Runnable task)
        throws MojoExecutionException {

        PrintStream stdout = System.out;
        Properties oldSystemProperties = System.getProperties();
        IsolatedThreadGroup threadGroup = new IsolatedThreadGroup("clojure-thread-group");
	try {
            Thread mainThread = new Thread(threadGroup, task, "clojure-main-thread");
            mainThread.setContextClassLoader(classloader);
            System.setOut(new PrintStream(new WrappedStream(stdout)));
            mainThread.start();
            joinNonDaemonThreads(threadGroup);
	} catch(Exception e) {
	    throw new MojoExecutionException("Clojure execution failed", e);
        } finally {
            System.setOut(stdout);
            if (oldSystemProperties != null) {
                System.setProperties(oldSystemProperties);
            }
	}
        if (threadGroup.uncaught != null) {
            throw new MojoExecutionException("Clojure execution failed", threadGroup.uncaught);
        }
    }

    private void joinNonDaemonThreads(ThreadGroup group) throws Exception {
        boolean found;
        do {
            found = false;
            Collection<Thread> threads = getThreads(group);
            for (Iterator iter = threads.iterator(); iter.hasNext(); ) {
                Thread thread = (Thread)iter.next();
                if (!thread.isAlive()) continue;
                if (thread.isDaemon()) continue;
                found = true;
                getLog().debug("Joining thread " + thread.toString());
                thread.join();
            }
            // more threads might have started while we weren't looking
        } while (found);
    }

    private static Collection<Thread> getThreads(ThreadGroup group) {
        Thread[] threads = new Thread[group.activeCount()];
        int count = group.enumerate(threads, true);
        Collection<Thread> result = new ArrayList<Thread>(count);
        for (int i = 0; i < threads.length && threads[i] != null; i++) {
            result.add(threads[i]);
        }
        return result;
    }

    class IsolatedThreadGroup extends ThreadGroup {
        public Throwable uncaught;

        public IsolatedThreadGroup(String name) {
            super(name);
        }

        public void uncaughtException(Thread thread, Throwable throwable) {
            if (throwable instanceof ThreadDeath) {
                // Normal thread completion
                return;
            }
            synchronized (this) {
                if (this.uncaught == null) {
                    this.uncaught = throwable;
                }
            }
            getLog().error("Exception in IsolatedThreadGroup:", throwable);
        }
    }

    class WrappedStream extends PrintStream {
        public WrappedStream(OutputStream stream) {
            super(stream);
        }

        public void close() {
            // do nothing
        }
    }
}

