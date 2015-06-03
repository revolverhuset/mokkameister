(defproject mokkameister "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire "5.3.1"]
                 [clj-http "1.1.2"]
                 [compojure "1.1.8"]
                 [environ "1.0.0"]
                 [liberator "0.13"]
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-devel "1.2.2"]
                 [ring/ring-jetty-adapter "1.2.2"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-ring "0.9.4"]]
  :ring {:handler mokkameister.web/handler}
  :hooks [environ.leiningen.hooks]
  :min-lein-version "2.0.0"
  :profiles {:uberjar {:main mokkameister.web, :aot :all}}
  :uberjar-name "mokkameister-standalone.jar")
