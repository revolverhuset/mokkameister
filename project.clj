(defproject mokkameister "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-devel "1.2.2"]
                 [environ "1.0.0"]
                 [cheshire "5.3.1"]
                 [clj-http "1.1.2"]]
  :plugins [[lein-environ "1.0.0"]]
  :hooks [environ.leiningen.hooks]
  :min-lein-version "2.0.0"
  :uberjar-name "kaffitraktar-standalone.jar")
