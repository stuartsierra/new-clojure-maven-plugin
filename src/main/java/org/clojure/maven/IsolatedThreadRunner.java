package org.clojure.maven;

import org.apache.maven.plugin.logging.Log;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.net.URLClassLoader;

public class IsolatedThreadRunner implements Runnable {
    private final Log log;
    private final Classpath classpath;
    private final Runnable task;
    private Throwable uncaught;

    public IsolatedThreadRunner(Log log, Classpath classpath, Runnable task) {
        this.log = log;
        this.classpath = classpath;
        this.task = task;
    }

    public void run() {
        URLClassLoader classloader = classpath.getClassLoader();
        log.debug("Classpath URLs: " + Arrays.toString(classloader.getURLs()));
        Properties oldSystemProperties = System.getProperties();
        IsolatedThreadGroup threadGroup = new IsolatedThreadGroup("isolated-thread-group");
	try {
            Thread mainThread = new Thread(threadGroup, task, "isolated-main-thread");
            mainThread.setContextClassLoader(classloader);
            mainThread.start();
            joinNonDaemonThreads(threadGroup);
        } finally {
            if (oldSystemProperties != null) {
                System.setProperties(oldSystemProperties);
            }
	}
        if (threadGroup.uncaught != null) {
            uncaught = threadGroup.uncaught;
        }
    }

    public Throwable getUncaught() {
        return uncaught;
    }

    private void joinNonDaemonThreads(ThreadGroup group) {
        boolean found;
        do {
            found = false;
            Collection<Thread> threads = getThreads(group);
            for (Iterator iter = threads.iterator(); iter.hasNext(); ) {
                Thread thread = (Thread)iter.next();
                if (!thread.isAlive()) continue;
                if (thread.isDaemon()) continue;
                found = true;
                log.debug("Joining thread " + thread.toString());
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    log.error("Thread interrupted in isolated thread group", e);
                }
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
        private Throwable uncaught;
        private Log log;

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
            log.error("Exception in isolated thread group:", throwable);
        }
   }
}