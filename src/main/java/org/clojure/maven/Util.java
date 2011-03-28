package org.clojure.maven;

public class Util {
    private Util() {}

    public static String join(String[] args) {
        StringBuilder s = new StringBuilder();
        s.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            s.append(",");
            s.append(args[i]);
        }
        return s.toString();
    }
}