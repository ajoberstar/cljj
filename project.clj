(defproject org.ajoberstar/ike.cljj "0.3.0-SNAPSHOT"
  :description "Clojure to Java interop APIs"
  :url "https://github.com/ajoberstar/ike.cljj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha5"]
                 [org.clojure/tools.macro "0.1.2"]]
  :release-tasks [["vcs" "assert-committed"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]
                  ["vcs" "push"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.9.0"]]}})
