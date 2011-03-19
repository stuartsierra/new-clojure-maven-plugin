package org.clojure.maven;

import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.Collection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class Classpath {
    public static final int COMPILE_CLASSPATH = 1;
    public static final int COMPILE_SOURCES   = 1 << 1;
    public static final int RESOURCES         = 1 << 2;
    public static final int TEST_CLASSPATH    = 1 << 3;
    public static final int TEST_SOURCES      = 1 << 4;
    public static final int TEST_RESOURCES    = 1 << 5;
    public static final int RUNTIME_CLASSPATH = 1 << 6;

    private final List<String> paths;

    public Classpath(MavenProject project, int includeFlags, Collection<String> extra) throws Exception {
        Set<String> paths = new HashSet<String>();
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

        this.paths = new ArrayList<String>(paths);
    }

    public List<String> getPaths() {
        return paths;
    }

    public List<URL> getURLs() throws Exception {
        List<URL> urls = new ArrayList<URL>(paths.size());
        for (int i = 0; i < paths.size(); i++) {
            urls.add(new File(paths.get(i)).toURL());
        }
        return urls;
    }

    public ClassLoader getClassLoader() throws Exception {
        return URLClassLoader.newInstance((URL[])getURLs().toArray(new URL[0]));
    }

    public String toString() {
        return paths.toString();
    }
}