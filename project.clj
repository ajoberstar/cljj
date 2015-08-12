(defproject ike/ike.cljj "0.1.0-SNAPSHOT"
  :description "Clojure to Java interop APIs"
  :url "https://github.com/ike-tools/ike.cljj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/tools.macro "0.1.2"]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.7.0"]]}})
