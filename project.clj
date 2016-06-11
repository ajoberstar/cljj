(defproject ike/ike.cljj "0.2.4"
  :description "DEPRECATED: Use org.ajoberstar/ike.cljj"
  :url "https://github.com/ajoberstar/ike.cljj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.macro "0.1.2"]]
  :repositories [["clojars" {:url "http://clojars.org/repo"
                             :sign-releases false}]]
  :release-tasks [["vcs" "assert-committed"]
                  ["vcs" "tag"]
                  ["deploy" "clojars"]
                  ["vcs" "push"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.7.0"]]}})
