(defproject ike/ike.cljj "0.2.0-alpha5"
  :description "Clojure to Java interop APIs"
  :url "https://github.com/ike-tools/ike.cljj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.macro "0.1.2"]]
  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]
                  ["vcs" "push"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.7.0"]]}})
