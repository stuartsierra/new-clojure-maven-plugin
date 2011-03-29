(ns org.clojure.maven.compile)

(binding [*compile-path*
          (System/getProperty "clojure.compile.path" "target/classes")
          *warn-on-reflection*
          (= "true" (System/getProperty "clojure.warn-on-reflection" "false"))
          *unchecked-math*
          (= "true" (System/getProperty "clojure.unchecked-math" "false"))]
  (doseq [name (seq (.split (System/getProperty "org.clojure.maven.namespaces" "") ","))]
    (println "Compiling" name)
    (compile (symbol name))))
