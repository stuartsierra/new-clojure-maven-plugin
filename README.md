**New clojure-maven-plugin** : a smaller clojure-maven-plugin developed under the Clojure Contributor Agreement

Copyright (c) Stuart Sierra, 2010. All rights reserved.  The use and distribution terms for this software are covered by the Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html at the root of this distribution.  By using this software in any fashion, you are agreeing to be bound by the terms of this license.  You must not remove this notice, or any other, from this software.


Usage
========================================

The `compile` goal performs ahead-of-time (AOT) compilation of Clojure sources.  Namespaces to be AOT-compiled must be declared in the plugin's configuration like this:

    <configuration>
      <namespaces>
        <namespace>your.first.namespace</namespace>
        <namespace>your.second.namespace</namespace>
      </namespaces>
    </configuration>

The `testCompile` goal does the same thing, but for test sources.

Testing with `clojure.test` is not yet supported.


Examples
========================================

See [my pom.xml for Clojure](https://github.com/stuartsierra/clojure/blob/new-plugin/pom.xml) using this plugin.