package org.clojure.maven;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Classpath {
    public static final int COMPILE_CLASSPATH = 1;
    public static final int COMPILE_SOURCES   = 1 << 1;
    public static final int RESOURCES         = 1 << 2;
    public static final int TEST_CLASSPATH    = 1 << 3;
    public static final int TEST_SOURCES      = 1 << 4;
    public static final int TEST_RESOURCES    = 1 << 5;
    public static final int RUNTIME_CLASSPATH = 1 << 6;

    private final Set<String> paths;

    public Classpath(MavenProject project, int includeFlags, Collection<String> extra) throws Exception {
        paths = new HashSet<String>();
        if ((includeFlags & COMPILE_CLASSPATH) != 0)
            paths.addAll(project.getCompileClasspathElements());
        if ((includeFlags & COMPILE_SOURCES) != 0)
            paths.addAll(project.getCompileSourceRoots());
        if ((includeFlags & RESOURCES) != 0)
            paths.addAll(project.getResources());
        if ((includeFlags & TEST_CLASSPATH) != 0)
            paths.addAll(project.getTestClasspathElements());
        if ((includeFlags & TEST_SOURCES) != 0)
            paths.addAll(project.getTestCompileSourceRoots());
        if ((includeFlags & TEST_RESOURCES) != 0)
            paths.addAll(project.getTestResources());
        if ((includeFlags & RUNTIME_CLASSPATH) != 0)
            paths.addAll(project.getRuntimeClasspathElements());
        if (extra != null)
            paths.addAll(extra);
    }

    public static Classpath forScope(MavenProject project, String scope,
                                     boolean includeSources, boolean includeResources,
                                     Collection<String> extra) throws Exception {
        return new Classpath(project, flagsForScope(scope, includeSources, includeResources),
                             extra);
    }

    public List<String> getPaths() {
        return new ArrayList<String>(paths);
    }

    public List<URL> getURLs() throws Exception {
        List<URL> urls = new ArrayList<URL>(paths.size());
        String path;
        for (Iterator<String> iter = paths.iterator(); iter.hasNext(); ) {
            path = iter.next();
            urls.add(new File(path).toURL());
        }
        return urls;
    }

    public URLClassLoader getClassLoader() throws Exception {
        return URLClassLoader.newInstance((URL[])getURLs().toArray(new URL[0]),
                                          Thread.currentThread().getContextClassLoader());
    }

    public String toString() {
        return paths.toString();
    }

    private static int flagsForScope(String scope, boolean includeSources,
                                     boolean includeResources) {
        int flags;
        if ("compile".equals(scope)) {
            flags = Classpath.COMPILE_CLASSPATH;
            if (includeSources) flags |= Classpath.COMPILE_SOURCES;
            if (includeResources) flags |= Classpath.RESOURCES;
        } else if ("test".equals(scope)) {
            flags = Classpath.COMPILE_CLASSPATH | Classpath.TEST_CLASSPATH |
                Classpath.RUNTIME_CLASSPATH;
            if (includeSources) flags |= Classpath.COMPILE_SOURCES | Classpath.TEST_SOURCES;
            if (includeResources) flags |= Classpath.RESOURCES | Classpath.TEST_RESOURCES;
        } else if ("runtime".equals(scope)) {
            flags = Classpath.COMPILE_CLASSPATH | Classpath.RUNTIME_CLASSPATH;
            if (includeSources) flags |= Classpath.COMPILE_SOURCES;
            if (includeResources) flags |= Classpath.RESOURCES | Classpath.TEST_RESOURCES;
        } else {
            throw new IllegalArgumentException("Invalid classpath scope: " + scope);
        }
        return flags;
    }
}